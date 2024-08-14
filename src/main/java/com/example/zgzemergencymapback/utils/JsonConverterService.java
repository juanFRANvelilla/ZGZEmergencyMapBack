package com.example.zgzemergencymapback.utils;

import com.example.zgzemergencymapback.model.*;
import com.example.zgzemergencymapback.service.GoogleMapsService;
import com.example.zgzemergencymapback.service.IncidentResourceService;
import com.example.zgzemergencymapback.service.IncidentService;
import com.example.zgzemergencymapback.service.ResourceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Service
public class JsonConverterService {
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private GoogleMapsService googleMapsService;

    @Autowired
    private IncidentResourceService incidentResourceService;


    /*
     * Método que obtiene datos del json para crear objetos incident
     * y determinar si es necesario guardarlos en la base de datos
     */
    public List<Incident> getIncidentInfoFromJson(String json, IncidentStatusEnum status) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = objectMapper.readTree(json);
        JsonNode resultNode = root.path("result");

        List<Incident> incidentList = new ArrayList<>();
        for (JsonNode node : resultNode) {
            String fecha = node.path("fecha").asText();
            String[] dateTime = fecha.split("T");
            LocalDate date = LocalDate.parse(dateTime[0]);
            LocalTime time = LocalTime.parse(dateTime[1]);

            Incident incident = Incident.builder()
                    .date(date)
                    .time(time)
                    .status(status)
                    .build();

            Optional<Incident> incidentOptional = incidentService.getIncidentByDateAndTime(incident.getDate(), incident.getTime());
            // Si no hay ningun incident en la base de datos con esa fecha y hora
            // se termina de crear el objeto y guardar en la base de datos
            if(incidentOptional.isEmpty()){
                // Completar los datos del incidente
                incident = completeIncidentDataFromJson(incident, node);
                if(incident != null){
                    incidentList.add(incident);
                }

            }
            // Si existe un incident en la base de datos con esa fecha y hora
            // pero estaba abierto -> se cierra y actualizamos su duracion
            else if(incidentOptional.isPresent() && incidentOptional.get().getStatus().equals(IncidentStatusEnum.OPEN)){
                Incident incidentToUpdate = incidentOptional.get();
                incidentToUpdate.setStatus(IncidentStatusEnum.CLOSED);
                incidentToUpdate.setDuration(incidentOptional.get().getDuration());
                incidentService.saveIncident(incidentToUpdate);
                incidentList.add(incidentToUpdate);
            }
            // Caso en el que ya este registrado el incident y este cerrado, no se actualiza
        }
        return incidentList;
    }

    private static boolean checkCoordinates(CoordinatesAndAddress coordinatesAndAddress){
        if(coordinatesAndAddress.getCoordinates().get(0) == 41.6474339 && coordinatesAndAddress.getCoordinates().get(1) == -0.8861451){
            return false;
        } else {
            return true;
        }
    }

    private static String formatAddress(String address) {
//        address = "FEDERICO *escribir* OZANAM, FEDERICO (Zaragoza) esto es una, sdfsafe .cadena muy raaa, dsjfdslfj &*&(^*(&(*& Ñ";
        // Primero, eliminamos cualquier texto adicional como "*escribir*"
        String cleanedInput = address.replaceAll("\\*.*\\*", "").trim();

        // Luego, dividimos la cadena usando ", " como delimitador
        String[] parts = cleanedInput.split(", ");

        // Verifica que la división haya tenido éxito
        if (parts.length < 2) {
//            throw new IllegalArgumentException("Formato de cadena inesperado");
            return address;
        }

        // La primera parte contiene la ubicación y la segunda parte contiene el prefijo junto con la ciudad
        String location = parts[0].trim(); // "OZANAN"
        String remaining = parts[parts.length - 1].trim(); // "FEDERICO *escribir* OZANAM, FEDERICO (Zaragoza)"

        // Encontramos el índice del primer paréntesis
        int parenthesisIndex = remaining.indexOf('(');

        if (parenthesisIndex == -1) {
//            throw new IllegalArgumentException("No se encontró el paréntesis en la cadena");
            return address;
        }

        // Extraemos el prefijo y la ciudad
        String prefix = remaining.substring(0, parenthesisIndex).trim(); // "FEDERICO *escribir* OZANAM,"
        String city = remaining.substring(parenthesisIndex + 1).replace(")", "").trim(); // "Zaragoza"

        // Limpiamos cualquier texto adicional antes de la ciudad
        prefix = prefix.replaceAll(",.*", "").trim(); // "FEDERICO"

        // Reensamblamos la cadena formateada
        return prefix + " " + location + " (" + city + ")";
    }


    public Incident completeIncidentDataFromJson(Incident incident, JsonNode node) {
        String incidentType = node.path("tipoSiniestro").asText();
        incident.setIncidentType(incidentType);
        String address = node.path("direccion").asText();
        String duration = node.path("duracion").asText();
        incident.setDuration(duration);

        String coordinatesJsonResponse = googleMapsService.getcoordinates(address);
        CoordinatesAndAddress coordinatesAndAddress = getCoordinatesFromJson(coordinatesJsonResponse);

        // Manejar los casos en los que la api de google maps no devuelve la direccion de calle concreta, sino una generica de zaragoza
        if(!checkCoordinates(coordinatesAndAddress)){
            // Volver a intntar la llamada api formateando la direccion

            String formattedAddress = formatAddress(address);

            coordinatesJsonResponse = googleMapsService.getcoordinates(formattedAddress);
            coordinatesAndAddress = getCoordinatesFromJson(coordinatesJsonResponse);
            if(!checkCoordinates(coordinatesAndAddress)){
                coordinatesJsonResponse = googleMapsService.getcoordinates("calle " + formattedAddress);
                coordinatesAndAddress = getCoordinatesFromJson(coordinatesJsonResponse);
                if(!checkCoordinates(coordinatesAndAddress)){
                    return null;
                }
            }
        }
        Double latitude = coordinatesAndAddress.getCoordinates().get(0);
        Double longitude = coordinatesAndAddress.getCoordinates().get(1);
        incident.setLatitude(latitude);
        incident.setLongitude(longitude);

        address = coordinatesAndAddress.getAddress();
        incident.setAddress(address);

        List<Resource> resourceList = new ArrayList<>();
        JsonNode resourcesNode = node.path("recursos");
        for (JsonNode resourceNode : resourcesNode) {
            resourceService.checkResource(resourceNode.asText());
            Resource resource = resourceService.findResourceByName(resourceNode.asText());
            resourceList.add(resource);
        }

        //guardar incident sin resources para poder añadirlos después
        incidentService.saveIncident(incident);
        //agregar filas con las relaciones entre incident y resources
        incidentResourceService.addResourceToIncident(incident, resourceList);

        //obtener las relaciones entre incident y resources
        List<IncidentResource> incidentResourceList =  incidentResourceService.findIncidentResourceByIncident(incident);
        //añadir las relaciones al incident
        incident.setIncidentResources(incidentResourceList);
        //guardar el incident con las relaciones
        incidentService.saveIncident(incident);

        return incident;
    }


    public static CoordinatesAndAddress getCoordinatesFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = null;
        try {
            root = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode locationNode = root.path("results")
                .path(0);

        double lat = locationNode.path("geometry").path("location").path("lat").asDouble();
        double lng = locationNode.path("geometry").path("location").path("lng").asDouble();

        List<Double> coordinates = new ArrayList<>();
        coordinates.add(lat);
        coordinates.add(lng);

        // Obtener el nombre de la direccion de la respuesta api
        String adress = locationNode.path("address_components").path(0).path("long_name").asText();

        CoordinatesAndAddress coordinatesAndAddress = CoordinatesAndAddress
                .builder()
                .coordinates(coordinates)
                .address(adress)
                .build();

        return coordinatesAndAddress;
    }



}
