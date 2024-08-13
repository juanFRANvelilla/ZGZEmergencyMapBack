package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.CoordinatesAndAddress;
import com.example.zgzemergencymapback.model.Incident;
import com.example.zgzemergencymapback.model.IncidentResource;
import com.example.zgzemergencymapback.model.Resource;
import com.example.zgzemergencymapback.repository.IncidentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.zgzemergencymapback.utils.LocalTimeConverter.parseDurationToLocalTime;

@Service
public class IncidentService {
    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentResourceService incidentResourceService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private  GoogleMapsService googleMapsService;




    public List<Incident> convertJsonToIncidentsAndSaveInDb(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = objectMapper.readTree(json);
        JsonNode resultNode = root.path("result");

        List<Incident> incidentList = new ArrayList<>();

        for (JsonNode node : resultNode) {
            String fecha = node.path("fecha").asText();
            String[] dateTime = fecha.split("T");
            LocalDate date = LocalDate.parse(dateTime[0]);
            LocalTime time = LocalTime.parse(dateTime[1]);

            //buscar si ya hay incidente con esas fechas si no no hacer proceso
            if (incidentRepository.findByDateAndTime(date, time).isEmpty()) {
                String incidentType = node.path("tipoSiniestro").asText();
                String address = node.path("direccion").asText();
                String durationStr = node.path("duracion").asText();
                LocalTime duration = parseDurationToLocalTime(durationStr);

                CoordinatesAndAddress coordinatesAndAddress = googleMapsService.getcoordinates(address);

                Double latitude = coordinatesAndAddress.getCoordinates().get(0);
                Double longitude = coordinatesAndAddress.getCoordinates().get(1);
                address = coordinatesAndAddress.getAddress();

                List<Resource> resourceList = new ArrayList<>();
                JsonNode resourcesNode = node.path("recursos");
                for (JsonNode resourceNode : resourcesNode) {
                    resourceService.checkResource(resourceNode.asText());
                    Resource resource = resourceService.findResourceByName(resourceNode.asText());
                    resourceList.add(resource);
                }

                Incident incident = Incident.builder()
                        .date(date)
                        .time(time)
                        .incidentType(incidentType)
                        .address(address)
                        .duration(duration)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();


                //guardar incident sin resources para poder añadirlos después
                incidentRepository.save(incident);
                //agregar filas con las relaciones entre incident y resources
                incidentResourceService.addResourceToIncident(incident, resourceList);

                //obtener las relaciones entre incident y resources
                List<IncidentResource> incidentResourceList =  incidentResourceService.findIncidentResourceByIncident(incident);
                //añadir las relaciones al incident
                incident.setIncidentResources(incidentResourceList);
                //guardar el incident con las relaciones
                incidentRepository.save(incident);
                incidentList.add(incident);
            }
        }

        return incidentList;
    }



}
