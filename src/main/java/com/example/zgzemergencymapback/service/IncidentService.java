package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.*;
import com.example.zgzemergencymapback.repository.IncidentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;


@Service
public class IncidentService {
    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<Incident> getIncidentByDateAndTime(LocalDate date, LocalTime time) {
        return incidentRepository.findByDateAndTime(date, time);
    }


    public void saveIncident(Incident incident) {
        incidentRepository.save(incident);
    }

    @Transactional
    public void deleteAllIncident(){
        incidentRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE incident_id_seq RESTART WITH 1");
    }

}
