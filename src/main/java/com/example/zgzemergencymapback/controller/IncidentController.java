package com.example.zgzemergencymapback.controller;


import com.example.zgzemergencymapback.response.IncidentResponseDTO;
import com.example.zgzemergencymapback.service.IncidentResourceService;
import com.example.zgzemergencymapback.service.IncidentService;
import com.example.zgzemergencymapback.service.IncidentsZgzDataService;
import com.example.zgzemergencymapback.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentController {

    @Autowired
    private IncidentsZgzDataService incidentsZgzDataService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private IncidentResourceService incidentResourceService;

    /*
        * Método que obtiene los datos de los incidentes abiertos y cerrados de la API del Ayuntamiento de Zaragoza
        * y devuelve aquellos incident que se han guardado o actualizado en la base de datos
     */
    @GetMapping("/getEmergency")
    public Object getEmergencyData() {
        return incidentsZgzDataService.getIncidentData();
    }

    @CrossOrigin(origins = "http://192.168.0.128:8081", allowCredentials = "true")
    @GetMapping("/getTodayIncident")
    public IncidentResponseDTO getTodayIncidentData() {
        return incidentsZgzDataService.getTodayIncidentData();
    }

    @CrossOrigin(origins = "http://192.168.0.128:8081", allowCredentials = "true")
    @GetMapping("/getIncidentByDate")
    public IncidentResponseDTO getIncidentByDate(@Param("date") String date) {
        return incidentsZgzDataService.getIncidentByDate(date);
    }

    @GetMapping("/deleteAll")
    public void deleteAll() {
        incidentResourceService.deleteAllIncidentResource();
        resourceService.deleteAllResources();
        incidentService.deleteAllIncident();
    }

}
