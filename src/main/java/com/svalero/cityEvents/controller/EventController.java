package com.svalero.cityEvents.controller;

import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.exception.ErrorResponse;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.service.EventService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/events")
    public ResponseEntity<List<EventOutDto>> getAll(@RequestParam(value = "category", defaultValue = "") String category) {
        List<Event> allEvents;

        if (!category.isEmpty()){
            allEvents = eventService.findByCategory(category);
        } else {
            allEvents = eventService.findAll();
        }

        //Le decimos que nos mapee la lista de juegos a una lista con el objeto Dto que queremos mostrar. Y que mapee campo a campo los que coincidan
        List<EventOutDto> eventsOutDto = modelMapper.map(allEvents, new TypeToken<List<EventOutDto>>() {}.getType());


        return ResponseEntity.ok(eventsOutDto);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable long id) throws EventNotFoundException {
        Event event = eventService.findById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/events")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        Event newEvent = eventService.add(event);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<Event> modifyEvent(@PathVariable long id, @RequestBody Event event) throws EventNotFoundException {
        Event newEvent = eventService.modify(id,event);
        return ResponseEntity.ok(newEvent);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable long id) throws EventNotFoundException {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EventNotFoundException enfe) {
        ErrorResponse errorResponse = new ErrorResponse(404,"not-found","The event does not exist");
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }
}
