package com.example.zgzemergencymapback.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "incident_resource")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "incident_id")
    private Incident incident;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

}