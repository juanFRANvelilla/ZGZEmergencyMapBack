package com.example.zgzemergencymapback.repository;

import com.example.zgzemergencymapback.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

}
