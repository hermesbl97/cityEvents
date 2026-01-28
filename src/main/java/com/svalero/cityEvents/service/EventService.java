package com.svalero.cityEvents.service;

import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.EventInDto;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.repository.EventRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Event add(Location location , EventInDto eventInDto, List<Artist> artists){
        Event event = new Event(); //creamos un evento con lo que recibimos
        event.setLocation(location);
        event.setArtists(artists);

        modelMapper.map(eventInDto, event);
        return eventRepository.save(event);
    }

    public void delete(long id) throws EventNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        eventRepository.delete(event);
    }

    public List<EventOutDto> findAll(String category, String locationName, Float price) {
        List<Event> allEvents;

        if (category != null && !category.isEmpty()){
            allEvents = eventRepository.findByCategory(category);
        } else if (locationName != null && !locationName.isEmpty()){
            allEvents = eventRepository.findByLocation_Name(locationName);
        } else if (price != null) {
            allEvents = eventRepository.findByPriceLessThanEqualOrderByPriceAsc(price);
        } else {
            allEvents = eventRepository.findAll();
        }

        //Le decimos que nos mapee la lista de juegos a una lista con el objeto Dto que queremos mostrar. Y que mapee campo a campo los que coincidan
        return modelMapper.map(allEvents, new TypeToken<List<EventOutDto>>() {}.getType());
    }

    public Event findById(long id) throws EventNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        return event;
    }

    public Event modify(long id, Event event) throws EventNotFoundException {
        Event eventExisting = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        modelMapper.map(event, eventExisting);
        eventExisting.setId(id);

        return eventRepository.save(eventExisting);
    }


}
