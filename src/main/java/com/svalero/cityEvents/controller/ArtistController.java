package com.svalero.cityEvents.controller;

import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.dto.ArtistOutDto;
import com.svalero.cityEvents.exception.ArtistNotFoundException;
import com.svalero.cityEvents.exception.ErrorResponse;
import com.svalero.cityEvents.service.ArtistService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ArtistController {

    @Autowired
    private ArtistService artistService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/artists")
    public ResponseEntity<List<ArtistOutDto>> getAll (
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "orderByFollowers", required = false) Boolean orderByFollowers) {

        List<ArtistOutDto> allArtists = artistService.findAll(type, active, orderByFollowers);

        return ResponseEntity.ok(allArtists);
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable long id) throws ArtistNotFoundException {
        Artist artist = artistService.findArtistById(id);
        return ResponseEntity.ok(artist);
    }

    @PostMapping("/artists")
    public ResponseEntity<Artist> addArtist(@Valid @RequestBody Artist artist) {
        Artist newArtist = artistService.add(artist);
        return new ResponseEntity<>(newArtist, HttpStatus.CREATED);
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> modifyArtist(@PathVariable long id, @RequestBody Artist artist)
            throws ArtistNotFoundException {
        Artist newArtist = artistService.modify(id, artist);
        return ResponseEntity.ok(newArtist);
    }

    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable long id) throws ArtistNotFoundException {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ArtistNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ArtistNotFoundException anfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The artist does not exist");
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
