package com.example.zgzemergencymapback.utils;

import com.example.zgzemergencymapback.model.Incident;
import com.example.zgzemergencymapback.model.Resource;
import com.example.zgzemergencymapback.service.ResourceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonConverter {
    @Autowired
    private ResourceService resourceService;

    public static LocalTime parseDurationToLocalTime(String durationStr) {
        // Elimina cualquier espacio extra
        durationStr = durationStr.trim().replaceAll(" +", " ");

        // Ejemplo: "0 h 32 m" -> "0 h 32 m"
        String[] parts = durationStr.split(" ");

        int hours = 0;
        int minutes = 0;

        // Se espera que las horas estén en la posición 0 y los minutos en la posición 2
        if (parts.length == 4) {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[2]);
        } else if (parts.length == 2) {
            // Si no hay horas, solo minutos (ejemplo: "32 m")
            if (parts[1].equals("m")) {
                minutes = Integer.parseInt(parts[0]);
            }
        }

        // Suma las horas y minutos a medianoche
        return LocalTime.MIDNIGHT.plusHours(hours).plusMinutes(minutes);
    }
}
