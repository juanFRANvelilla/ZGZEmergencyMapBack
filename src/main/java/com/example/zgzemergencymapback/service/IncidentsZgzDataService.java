package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.Incident;
import com.example.zgzemergencymapback.model.IncidentStatusEnum;
import com.example.zgzemergencymapback.utils.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class IncidentsZgzDataService {
    private final RestTemplate restTemplate;

    @Autowired
    JsonConverter jsonConverter;

    public IncidentsZgzDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*
      * Método que obtiene los datos de los incidentes cerrados de la API de Zaragoza
      * y los guarda en la base de datos, devuelve la lista de incident que se han
      * introducido en la base de datos o incidentes que se han cerrado
     */
    public List<Incident> getCloseIncidentData() {
        String url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=21&rf=markdown";
        List<Incident> incidentList = new ArrayList<>();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // Convertir json a objetos incident, y llevar a cabo la logica para
            // guardar en la base de datos, devuelve la lista de incident que se han
            // introducido en la base de datos o incidentes que se han cerrado
            incidentList = jsonConverter.getIncidentInfoFromJson(jsonResponse, IncidentStatusEnum.CLOSED);

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
    public List<Incident> getOpenIncidentData() {
        String url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=10&rf=markdown";
        List<Incident> incidentList = new ArrayList<>();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // Convertir el JSON a objetos Incident y guardarlos en la base de datos
            // devuelve la lista de incident que se han introducido en la base de datos
            incidentList = jsonConverter.getIncidentInfoFromJson(jsonResponse, IncidentStatusEnum.OPEN);

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

}
