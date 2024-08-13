package com.example.zgzemergencymapback.utils;

import java.time.LocalTime;

public class LocalTimeConverter {
    public static LocalTime parseDurationToLocalTime(String durationStr) {
        durationStr = durationStr.trim().replaceAll(" +", " ");
        String[] parts = durationStr.split(" ");

        int hours = 0;
        int minutes = 0;

        if (parts.length == 4) {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[2]);
        } else if (parts.length == 2) {
            if (parts[1].equals("m")) {
                minutes = Integer.parseInt(parts[0]);
            }
        }

        return LocalTime.MIDNIGHT.plusHours(hours).plusMinutes(minutes);
    }
}
