package com.svalero.cityEvents.controller;

import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.domain.Review;
import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.dto.ReviewInDto;
import com.svalero.cityEvents.exception.*;
import com.svalero.cityEvents.service.EventService;
import com.svalero.cityEvents.service.LocationService;
import com.svalero.cityEvents.service.ReviewService;
import com.svalero.cityEvents.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getAll(@RequestParam(value = "username", defaultValue = "") String username) {
        List<Review> allReviews;

        if (!username.isEmpty()) {
            allReviews = reviewService.findByUsername(username);
        } else {
            allReviews = reviewService.findAll();
        }

        return ResponseEntity.ok(allReviews);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable long id) throws ReviewNotFoundException {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/reviews")
    public ResponseEntity<Review> addReview(@RequestBody ReviewInDto reviewInDto) throws EventNotFoundException, UserNotFoundException {
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
        ErrorResponse errorResponse = new ErrorResponse(404, "not-found","The review does not exist");
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }
}
