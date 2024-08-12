package com.example.zgzemergencymapback.controller;


import com.example.zgzemergencymapback.service.IncidentsZgzDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestApiController {

    @Autowired
    private IncidentsZgzDataService incidentsZgzDataService;



    @GetMapping("/getEmergency")
    public Object getEmergencyData() {
        return incidentsZgzDataService.getIncidentData();
    }

}
