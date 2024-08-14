package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.*;
import com.example.zgzemergencymapback.repository.IncidentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;


@Service
public class IncidentService {
    @Autowired
    private IncidentRepository incidentRepository;

    public Optional<Incident> getIncidentByDateAndTime(LocalDate date, LocalTime time) {
        return incidentRepository.findByDateAndTime(date, time);
    }


    public void saveIncident(Incident incident) {
        incidentRepository.save(incident);
    }

    @Transactional
    public void deleteAllIncident(){
        incidentRepository.deleteAll();
    }

}
