package com.example.zgzemergencymapback.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.zgzemergencymapback.model.*;
import com.example.zgzemergencymapback.utils.JsonConverter;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class IncidentsZgzDataServiceTest {
    @Mock
    private IncidentService incidentService;
    @Mock
    private ResourceService resourceService;
    @Mock
    private IncidentResourceService incidentResourceService;
    @Mock
    private GoogleMapsService googleMapsService;
    @InjectMocks
    private JsonConverter jsonConverter;


    @Test
    void testGetIncidentInfoFromJson2() throws IOException {
        // Dado este objeto json
        String json = "{\n" +
                "  \"totalCount\": 1,\n" +
                "  \"start\": 0,\n" +
                "  \"rows\": 1,\n" +
                "  \"fecha\": \"2024-08-14T00:00:00\",\n" +
                "  \"tipo\": \"10\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"fecha\": \"2024-08-13T22:24:04\",\n" +
                "      \"tipoSiniestro\": \"Caída de árboles, etc\",\n" +
                "      \"direccion\": \"camino monzalbarba (Zaragoza)\",\n" +
                "      \"duracion\": \"0 h  55 m\",\n" +
                "      \"recursos\": [\n" +
                "        \"Bomba pesada mixta\",\n" +
                "        \"Bomba nodriza pesada\",\n" +
                "        \"Autoescala automática 30 m.\"\n" +
                "      ],\n" +
                "      \"tipo\": \"10\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        IncidentStatusEnum status = IncidentStatusEnum.OPEN;

        // Mocking
        // No existe un incidente con esa fecha y hora
        when(incidentService.getIncidentByDateAndTime(any(LocalDate.class), any(LocalTime.class))).thenReturn(Optional.empty());
        when(googleMapsService.getcoordinates(anyString())).thenReturn("{\"results\": [{\"geometry\": {\"location\": {\"lat\": 40.7128, \"lng\": -74.0060}}, \"address_components\": [{\"long_name\": \"123 Test St\"}]}]}");
        // Crear lista con 3 IncidentResource simulando que son las clases que se encuentrar al buscar en la base de datos
        List<IncidentResource> incidentResourceList = Arrays.asList(
                new IncidentResource(),
                new IncidentResource(),
                new IncidentResource()
        );

        when(incidentResourceService.findIncidentResourceByIncident(any(Incident.class)))
                .thenReturn(incidentResourceList);



        // Llamamos al método a testear
        List<Incident> result = jsonConverter.getIncidentInfoFromJson(json, status);

        assertEquals(1, result.size());

        Incident firstIncident = result.get(0);
        assertEquals("Caída de árboles, etc", firstIncident.getIncidentType());
        assertEquals(40.7128, firstIncident.getLatitude());
        assertEquals(-74.0060, firstIncident.getLongitude());
        assertEquals(status, firstIncident.getStatus());


        // LLamada a los métodos de los mocks
        verify(incidentService, times(2)).saveIncident(any(Incident.class));
        verify(incidentResourceService, times(1)).addResourceToIncident(any(Incident.class), anyList());
    }

}
