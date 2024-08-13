package com.example.zgzemergencymapback.controller;


import com.example.zgzemergencymapback.model.Incident;
import com.example.zgzemergencymapback.response.IncidentResponseDTO;
import com.example.zgzemergencymapback.service.IncidentsZgzDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
public class IncidentController {

    @Autowired
    private IncidentsZgzDataService incidentsZgzDataService;


    /*
        * Método que obtiene los datos de los incidentes abiertos y cerrados de la API del Ayuntamiento de Zaragoza
        * y devuelve aquellos incident que se han guardado o actualizado en la base de datos
     */
    @GetMapping("/getEmergency")
    public Object getEmergencyData() {
        List<Incident> incidentOpenList =  incidentsZgzDataService.getOpenIncidentData();
        List<Incident> incidentCloseList =  incidentsZgzDataService.getCloseIncidentData();

        incidentCloseList.addAll(incidentOpenList);
        return incidentCloseList;
    }

    @GetMapping("/getTodayIncidentData")
    public IncidentResponseDTO getTodayIncidentData() {
        return incidentsZgzDataService.getTodayIncidentData();
    }

}
