package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.Incident;
import com.example.zgzemergencymapback.model.IncidentStatusEnum;
import com.example.zgzemergencymapback.repository.IncidentRepository;
import com.example.zgzemergencymapback.response.IncidentResponseDTO;
import com.example.zgzemergencymapback.utils.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class IncidentsZgzDataService {
    private final RestTemplate restTemplate;

    @Autowired
    JsonConverter jsonConverter;

    @Autowired
    IncidentRepository incidentRepository;

    public IncidentsZgzDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public IncidentResponseDTO getTodayIncidentData() {
        LocalDate date = LocalDate.now();
        List<Incident> incidentList = incidentRepository.findTodayIncident(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return IncidentResponseDTO
                .builder()
                .date(date.format(formatter))
                .size(incidentList.size())
                .incidentList(incidentList)
                .build();
    }

    /*
      * Método que obtiene los datos de los incidentes de la API de Zaragoza
      * y los guarda en la base de datos, devuelve la lista de incident que se han
      * introducido en la base de datos o incidentes que se han cerrado
     */
    public List<Incident> getIncidentData() {
        String url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=21&rf=markdown";
        List<Incident> incidentList = new ArrayList<>();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // Convertir json a objetos incident cerrados, y llevar a cabo la logica para
            // guardar en la base de datos, devuelve la lista de incident que se han
            // introducido en la base de datos o incidentes que se han cerrado
            incidentList = jsonConverter.getIncidentInfoFromJson(jsonResponse, IncidentStatusEnum.CLOSED);


            url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=10&rf=markdown";
            jsonResponse = restTemplate.getForObject(url, String.class);
            // Convertir el JSON a objetos Incident abiertos y guardarlos en la base de datos
            // devuelve la lista de incident que se han introducido en la base de datos
            incidentList.addAll(jsonConverter.getIncidentInfoFromJson(jsonResponse, IncidentStatusEnum.OPEN));

        } catch (RestClientException e) {
            System.err.println("Error al hacer la llamada a la API: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // Maneja errores relacionados con la conversión del JSON
            System.err.println("Error al procesar el JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Maneja cualquier otro tipo de error no previsto
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        return incidentList;
    }

    /*
      * Método que obtiene los datos de los incidentes abiertos de la API de Zaragoza
      * y los guarda en la base de datos, devuelve la lista de incident que se han
      * introducido en la base de datos
     */
//    public List<Incident> getOpenIncidentData() {
//        String url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=10&rf=markdown";
//        List<Incident> incidentList = new ArrayList<>();
//        try {
//            String jsonResponse = restTemplate.getForObject(url, String.class);
//
//            // Convertir el JSON a objetos Incident y guardarlos en la base de datos
//            // devuelve la lista de incident que se han introducido en la base de datos
//            incidentList = jsonConverter.getIncidentInfoFromJson(jsonResponse, IncidentStatusEnum.OPEN);
//
//        } catch (RestClientException e) {
//            System.err.println("Error al hacer la llamada a la API: " + e.getMessage());
//            e.printStackTrace();
//        } catch (JsonProcessingException e) {
//            // Maneja errores relacionados con la conversión del JSON
//            System.err.println("Error al procesar el JSON: " + e.getMessage());
//            e.printStackTrace();
//        } catch (Exception e) {
//            // Maneja cualquier otro tipo de error no previsto
//            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return incidentList;
//    }

}
