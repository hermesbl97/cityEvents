package com.svalero.cityEvents.service;

import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.exception.LocationNotFoundException;
import com.svalero.cityEvents.repository.LocationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository; //con el autowired conectamos la capa controller con el repository
    @Autowired
    private ModelMapper modelMapper;

    public Location add(Location location){
        return locationRepository.save(location);
    }

    public void delete(long id) throws LocationNotFoundException {
        Location location = locationRepository.findById(id)
                .orElseThrow(LocationNotFoundException::new);

        locationRepository.delete(location);
    }

    public List<Location> findAll() {
        List<Location> allLocations = locationRepository.findAll();
        return allLocations;
    }

    public List<Location> findByCategory(String category) {
        List<Location> locations = locationRepository.findByCategory(category);
        return locations;
    }

    public List<Location> findByDisabledAccessLocation() {
        List<Location> locations = locationRepository.findByDisabledAccessTrue();
        return locations;
    }

    public Location findById(long id) throws  LocationNotFoundException {
        Location location = locationRepository.findById(id)
                .orElseThrow(LocationNotFoundException::new);
        return location;
    }

    public Location modify(long id, Location location) throws LocationNotFoundException {
        Location existingLocation =locationRepository.findById(id)
                .orElseThrow(LocationNotFoundException::new);

        modelMapper.map(location,existingLocation);
        existingLocation.setId(id); //le definimos el id para que no nos cuele el id 0 de location

        return locationRepository.save(existingLocation);
    }
}
