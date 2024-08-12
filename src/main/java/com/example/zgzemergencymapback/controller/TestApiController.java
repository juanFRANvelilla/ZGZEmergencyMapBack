package com.example.zgzemergencymapback.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestApiController {

    private final RestTemplate restTemplate;

    @Autowired
    public TestApiController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/getEmergency")
    public Object getEmergencyData() {
        String url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=20&rf=markdown";
        return restTemplate.getForObject(url, Object.class);
    }

}
