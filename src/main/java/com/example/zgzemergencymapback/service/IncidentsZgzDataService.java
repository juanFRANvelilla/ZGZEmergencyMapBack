package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.Incident;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IncidentsZgzDataService {

    private final RestTemplate restTemplate;

    @Autowired
    IncidentService incidentService;

//    @Value("${zaragoza.api.url}")
//    private String apiUrl;
//
//    @Value("${zaragoza.api.key}")
//    private String apiKey;
//
//    public ZaragozaApiService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

    public IncidentsZgzDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void updateIncidentData() {
        List<Incident> incidentList = new ArrayList<>();
    }

    public Object getIncidentData() {
        String url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=20&rf=markdown";
        List<Incident> incidentList = new ArrayList<>();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // Convertir el JSON a objetos Incident y guardarlos en la base de datos
            incidentList = incidentService.convertJsonToIncidentsAndSaveInDb(jsonResponse);

        } catch (RestClientException e) {
            // Maneja errores relacionados con la conexión o la llamada HTTP
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

}
