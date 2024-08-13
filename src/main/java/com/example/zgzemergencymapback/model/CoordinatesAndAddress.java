package com.example.zgzemergencymapback.model;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinatesAndAddress {
    private List<Double> coordinates;
    private String address;
}
