package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.CoordinatesAndAddress;
import com.example.zgzemergencymapback.utils.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;



@Service
public class GoogleMapsService {
    public static final String CONFIG_FILE = "src/main/java/com/example/zgzemergencymapback/config/ApiKey.json";
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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(new File(CONFIG_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String apiKey = root.path("API_KEY_GOOGLE_MAPS").asText();
        System.out.println("Google Maps API Key: " + apiKey);
        String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";
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
