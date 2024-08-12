package com.example.zgzemergencymapback.repository;

import com.example.zgzemergencymapback.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    @Query("SELECT i FROM Incident i WHERE i.date = :date AND i.time = :time")
    Optional<Incident> findByDateAndTime(@Param("date") LocalDate date, @Param("time") LocalTime time);

}
