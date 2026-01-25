package com.svalero.cityEvents.controller;

import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.domain.Review;
import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.dto.ReviewInDto;
import com.svalero.cityEvents.dto.ReviewOutDto;
import com.svalero.cityEvents.exception.*;
import com.svalero.cityEvents.service.EventService;
import com.svalero.cityEvents.service.LocationService;
import com.svalero.cityEvents.service.ReviewService;
import com.svalero.cityEvents.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.rmi.ServerError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewOutDto>> getAll(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "eventName", required = false) String eventName,
            @RequestParam(value = "rate", required = false) Float rate) {

        List<Review> allReviews;

        if (username != null && !username.isEmpty()) {
         allReviews = reviewService.findByUsername(username);
        } else if (eventName != null && !eventName.isEmpty()) {
            allReviews = reviewService.findByEventName(eventName);
        } else if (rate != null) {
            allReviews = reviewService.findByRate(rate);
        } else {
            allReviews = reviewService.findAll();
        }

        List<ReviewOutDto> reviewsOutDto = modelMapper.map(allReviews, new TypeToken<List<ReviewOutDto>>() {}.getType());

        return ResponseEntity.ok(reviewsOutDto);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable long id) throws ReviewNotFoundException {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/reviews")
    public ResponseEntity<Review> addReview(@Valid @RequestBody ReviewInDto reviewInDto) throws EventNotFoundException, UserNotFoundException {
        Event event = eventService.findById(reviewInDto.getEventId());
        User user = userService.findUserById(reviewInDto.getUserId());

        Review newReview = reviewService.add(reviewInDto, event, user);

        return new ResponseEntity<>(newReview,HttpStatus.CREATED);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<Review> modifyReview(@PathVariable long id, @RequestBody Review review) throws ReviewNotFoundException {
        Review newReview = reviewService.modify(id, review);
        return ResponseEntity.ok(newReview);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable long id) throws ReviewNotFoundException {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ReviewNotFoundException rnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The review does not exist");
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The user does not exist");
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
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
