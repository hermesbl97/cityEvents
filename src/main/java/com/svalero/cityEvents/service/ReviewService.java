package com.svalero.cityEvents.service;

import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.domain.Review;
import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.dto.ReviewInDto;
import com.svalero.cityEvents.exception.ReviewNotFoundException;
import com.svalero.cityEvents.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Review add(ReviewInDto reviewInDto, Event event, User user) {
        Review review = new Review(); //creamos una review con all lo que recibimos
        review.setEvent(event);
        review.setUser(user);

        modelMapper.map(reviewInDto, review);

        return reviewRepository.save(review);
    }

    public void delete(long id) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);
        reviewRepository.delete(review);
    }

    public List<Review> findAll(){
        List<Review> allReviews = reviewRepository.findAll();
        return allReviews;
    }

    public List<Review> findByUsername(String username) {
        List<Review> reviews = reviewRepository.findByUserUsername(username);
        return reviews;
    }

    public List<Review> findByEventName(String eventName) {
        return reviewRepository.findByEvent_Name(eventName);
    }

    public List<Review> findByRate(float rate) {
        List<Review> reviews = reviewRepository.findByRateGreaterThan(rate);
        return reviews;
    }

    public Review getReviewById(long id) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);

        return review;
    }

    public Review modify(long id,Review review) throws ReviewNotFoundException {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);

        modelMapper.map(review,existingReview);
        existingReview.setId(id);

        return reviewRepository.save(existingReview);
    }
}
