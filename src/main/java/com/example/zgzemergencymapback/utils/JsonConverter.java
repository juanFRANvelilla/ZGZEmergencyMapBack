package com.example.zgzemergencymapback.utils;

import com.example.zgzemergencymapback.model.CoordinatesAndAddress;
import com.example.zgzemergencymapback.service.ResourceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class JsonConverter {
    @Autowired
    private ResourceService resourceService;

    public CoordinatesAndAddress getCoordinatesFromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = objectMapper.readTree(json);

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
