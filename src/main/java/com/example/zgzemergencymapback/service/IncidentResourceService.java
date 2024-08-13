package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.Incident;
import com.example.zgzemergencymapback.model.IncidentResource;
import com.example.zgzemergencymapback.model.Resource;
import com.example.zgzemergencymapback.repository.IncidentResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentResourceService {
    @Autowired
    private IncidentResourceRepository incidentResourceRepository;


    public void addResourceToIncident(Incident incident, List<Resource> resourceList) {
        for (Resource resource : resourceList) {
            IncidentResource incidentResource = new IncidentResource();
            incidentResource.setIncident(incident);
            incidentResource.setResource(resource);
            incidentResourceRepository.save(incidentResource);
        }
    }

    public List<IncidentResource> findIncidentResourceByIncident(Incident incident) {
        return incidentResourceRepository.findByIncident(incident);
    }

}
