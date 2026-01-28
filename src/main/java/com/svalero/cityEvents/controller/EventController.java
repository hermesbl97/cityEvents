package com.svalero.cityEvents.controller;

import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.EventInDto;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.exception.ErrorResponse;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.exception.LocationNotFoundException;
import com.svalero.cityEvents.service.ArtistService;
import com.svalero.cityEvents.service.EventService;
import com.svalero.cityEvents.service.LocationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ArtistService artistService;

    @GetMapping("/events")
    public ResponseEntity<List<EventOutDto>> getAll(
            @RequestParam(value = "category", required = false) String category, //con required lo hacemos opcional
            @RequestParam(value = "locationName", required = false) String locationName,
            @RequestParam(value = "price", required = false) Float price) {

        List<EventOutDto> allEventsOutDto = eventService.findAll(category, locationName, price);

        return ResponseEntity.ok(allEventsOutDto);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable long id) throws EventNotFoundException {
        Event event = eventService.findById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/events")
    public ResponseEntity<Event> addEvent(@Valid @RequestBody EventInDto eventInDto) throws LocationNotFoundException {
        //Buscamos la localización
        Location location = locationService.findById(eventInDto.getLocationId());
//        Location location = locationService.findById(1);

        //Buscamos los artistas
//        List<Artist> artists = artistService.findAllArtistsById(new ArrayList<>(List.of(1L)));
        List<Artist> artists = artistService.findAllArtistsById(eventInDto.getArtistsIds());

        Event newEvent = eventService.add(location, eventInDto, artists);

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
        ErrorResponse errorResponse = ErrorResponse.notFound("The event does not exist");
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(LocationNotFoundException lnfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The event does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        //extraemos los errores de la excepción del fallo
        manve.getBindingResult().getAllErrors().forEach(error -> { //para cada error rellenamos el nombre del campo
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName,message); //asociamos cada error con su mensaje
        });
        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.internalServerError();
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
