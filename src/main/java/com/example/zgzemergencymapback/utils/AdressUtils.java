package com.example.zgzemergencymapback.utils;

import com.example.zgzemergencymapback.model.CoordinatesAndAddress;

public class AdressUtils {
    public static boolean checkCoordinates(CoordinatesAndAddress coordinatesAndAddress){
        return coordinatesAndAddress.getCoordinates().get(0) != 41.6474339 || coordinatesAndAddress.getCoordinates().get(1) != -0.8861451;
    }

    public static String formatAddress(String address) {
//        address = "FEDERICO *escribir* OZANAM, FEDERICO (Zaragoza) esto es una, sdfsafe .cadena muy raaa, dsjfdslfj &*&(^*(&(*& Ñ";
        // Primero, eliminamos cualquier texto adicional como "*escribir*"
        String cleanedInput = address.replaceAll("\\*.*\\*", "").trim();

        // Luego, dividimos la cadena usando ", " como delimitador
        String[] parts = cleanedInput.split(", ");

        // Verifica que la división haya tenido éxito
        if (parts.length < 2) {
            return address;
        }

        // La primera parte contiene la ubicación y la segunda parte contiene el prefijo junto con la ciudad
        String location = parts[0].trim(); // "OZANAN"
        String remaining = parts[parts.length - 1].trim(); // "FEDERICO *escribir* OZANAM, FEDERICO (Zaragoza)"

        // Encontramos el índice del primer paréntesis
        int parenthesisIndex = remaining.indexOf('(');

        if (parenthesisIndex == -1) {
            return address;
        }

        // Extraemos el prefijo y la ciudad
        String prefix = remaining.substring(0, parenthesisIndex).trim(); // "FEDERICO *escribir* OZANAM,"
        String city = remaining.substring(parenthesisIndex + 1).replace(")", "").trim(); // "Zaragoza"

        // Limpiamos cualquier texto adicional antes de la ciudad
        prefix = prefix.replaceAll(",.*", "").trim(); // "FEDERICO"

        // Reensamblamos la cadena formateada
        return prefix + " " + location + " (" + city + ")";
    }
}
