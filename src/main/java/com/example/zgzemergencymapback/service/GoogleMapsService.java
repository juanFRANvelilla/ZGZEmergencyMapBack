package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.CoordinatesAndAddress;
import com.example.zgzemergencymapback.utils.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;


@Service
public class GoogleMapsService {
    private final RestTemplate restTemplate;

    @Autowired
    private JsonConverter jsonConverter;

    public GoogleMapsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*
        * Método que obtiene las coordenadas de una dirección dada
     */
    public CoordinatesAndAddress getcoordinates(String address) {
        String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";
        String apiKey = "AIzaSyCQrDqn90BYUBqEXNWAv0hlid1UVCvhLLc";
        String url = baseUrl + "?address=" + address + "&key=" + apiKey;
        CoordinatesAndAddress coordinatesAndAddress = new CoordinatesAndAddress();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            coordinatesAndAddress = jsonConverter.getCoordinatesFromJson(jsonResponse);


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
        return coordinatesAndAddress;
    }
}
