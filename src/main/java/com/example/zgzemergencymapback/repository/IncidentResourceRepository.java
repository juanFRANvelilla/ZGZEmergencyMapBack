package com.example.zgzemergencymapback.repository;

import com.example.zgzemergencymapback.model.Incident;
import com.example.zgzemergencymapback.model.IncidentResource;
import com.example.zgzemergencymapback.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentResourceRepository extends JpaRepository<IncidentResource, Long> {
    // Método para encontrar todos los resources asociados con el incident
    List<IncidentResource> findByIncident(Incident incident);
}
