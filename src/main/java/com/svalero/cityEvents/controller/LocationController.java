package com.svalero.cityEvents.controller;

import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.dto.LocationOutDto;
import com.svalero.cityEvents.exception.ErrorResponse;
import com.svalero.cityEvents.exception.LocationNotFoundException;
import com.svalero.cityEvents.repository.LocationRepository;
import com.svalero.cityEvents.service.LocationService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/locations")
    public ResponseEntity<List<LocationOutDto>> getALL(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "disabledAccess", required = false) Boolean disabledAccess) { //indicamos que queremos que filtre por category la busqueda con el Request param

        List<Location> allLocations;

        if (category != null && !category.isEmpty()) {
            allLocations = locationService.findByCategory(category);
        } else if (disabledAccess != null && disabledAccess){
            allLocations = locationService.findByDisabledAccessLocation();
        } else {
            allLocations = locationService.findAll();
        }
        List<LocationOutDto> locationsOutDto = modelMapper.map(allLocations, new TypeToken<List<LocationOutDto>>() {}.getType());

        return ResponseEntity.ok(locationsOutDto);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable long id) throws LocationNotFoundException {
        Location location = locationService.findById(id);
        return ResponseEntity.ok(location);
    }

    @PostMapping("/locations")
    public ResponseEntity<Location> addLocation(@Valid @RequestBody Location location) { //me pasan el juego que quiero añadir en el body de la llamada
        Location newLocation = locationService.add(location);
        return new ResponseEntity<>(newLocation, HttpStatus.CREATED); //nos devuelve el juego posteado cuando se crea1
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<Location> modifyLocation(@PathVariable long id, @RequestBody Location location) throws LocationNotFoundException { //usamos el path variable para recoger el id del elemento que queremos modificar en el endpoint
       Location newLocation= locationService.modify(id, location);
//       return new ResponseEntity<>(newLocation, HttpStatus.OK);   Es identico a la siguiente linea
        return ResponseEntity.ok(newLocation);
    } //cuando se modifica la localización nos la devuelve

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable long id) throws LocationNotFoundException {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(LocationNotFoundException lnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The event does not exist");
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
