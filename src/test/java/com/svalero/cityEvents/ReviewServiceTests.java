package com.svalero.cityEvents;

import com.svalero.cityEvents.domain.*;
import com.svalero.cityEvents.dto.*;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.exception.ReviewNotFoundException;
import com.svalero.cityEvents.repository.ReviewRepository;
import com.svalero.cityEvents.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {

    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ReviewRepository reviewRepository;

    @Test
    public void testFindAll() {

        List<Review> mockReviewList = List.of(
            new Review(1, 4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2),
                  true, 35, true, null, null),
            new Review(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),
                    false, 22, true, null, null),
            new Review(3, 1.3f, "No lo recomiendo", LocalDate.of(2023,2,1), false,
                    1, false, null, null)
        );

        List<ReviewOutDto> mockReviewOutDto = List.of(
            new ReviewOutDto(1,4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2), 35, true),
            new ReviewOutDto(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),22, true),
            new ReviewOutDto(3, 1.3f, "No lo recomiendo", LocalDate.of(2023,2,1),1, false)
        );

        when(reviewRepository.findAll()).thenReturn(mockReviewList);
        when(modelMapper.map(mockReviewList, new TypeToken<List<ReviewOutDto>>() {}.getType())).thenReturn(mockReviewOutDto);

        List<ReviewOutDto> actualReviewList = reviewService.findAll("","",null);
        assertEquals(3, actualReviewList.size());
        assertEquals(35, actualReviewList.getFirst().getLikes());

        verify(reviewRepository, times(1)).findAll();
        verify(reviewRepository, times(0)).findByEvent_Name("");
        verify(reviewRepository, times(0)).findByUserUsername("");
        verify(reviewRepository, times(0)).findByRateGreaterThan(3);
    }

    @Test
    public void testFindByUserUsername() {

        User user = new User();
        user.setId(3);
        user.setUsername("martin123");

        List<Review> mockReviewList = List.of(
                new Review(1, 4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2),
                        true, 35, true, null, user),
                new Review(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),
                        false, 22, true, null, null),
                new Review(3, 1.3f, "No lo recomiendo", LocalDate.of(2023,2,1), false,
                        1, false, null, null)
        );

        List<ReviewOutDto> mockReviewOutDto = List.of(
                new ReviewOutDto(1,4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2), 35, true)
        );

        when(reviewRepository.findByUserUsername("martin123")).thenReturn(mockReviewList);
        when(modelMapper.map(mockReviewList, new TypeToken<List<ReviewOutDto>>() {}.getType())).thenReturn(mockReviewOutDto);

        List<ReviewOutDto> actualReviewList = reviewService.findAll("martin123","",null);
        assertEquals(1, actualReviewList.size());
        assertEquals(35, actualReviewList.getFirst().getLikes());

        verify(reviewRepository, times(0)).findAll();
        verify(reviewRepository, times(1)).findByUserUsername("martin123");
        verify(reviewRepository, times(0)).findByEvent_Name("");
        verify(reviewRepository, times(0)).findByRateGreaterThan(3);
    }

    @Test
    public void testFindByEventName() {

        Event event = new Event();
        event.setId(2);
        event.setName("Musical Navideño");

        List<Review> mockReviewList = List.of(
                new Review(1, 4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2),
                        true, 35, true, null, null),
                new Review(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),
                        false, 22, true, event, null),
                new Review(3, 1.3f, "No lo recomiendo", LocalDate.of(2023,2,1), false,
                        1, false, event, null)
        );

        List<ReviewOutDto> mockReviewOutDto = List.of(
                new ReviewOutDto(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),22, true),
                new ReviewOutDto(3, 1.3f, "No lo recomiendo", LocalDate.of(2023,2,1),1, false)
        );

        when(reviewRepository.findByEvent_Name("Musical Navideño")).thenReturn(mockReviewList);
        when(modelMapper.map(mockReviewList, new TypeToken<List<ReviewOutDto>>() {}.getType())).thenReturn(mockReviewOutDto);

        List<ReviewOutDto> actualReviewList = reviewService.findAll("","Musical Navideño",null);
        assertEquals(2, actualReviewList.size());
        assertEquals(22, actualReviewList.getFirst().getLikes());

        verify(reviewRepository, times(0)).findAll();
        verify(reviewRepository, times(1)).findByEvent_Name("Musical Navideño");
        verify(reviewRepository, times(0)).findByUserUsername("");
        verify(reviewRepository, times(0)).findByRateGreaterThan(3);
    }

    @Test
    public void testFindByRateGreaterThan() {

        List<Review> mockReviewList = List.of(
                new Review(1, 4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2),
                        true, 35, true, null, null),
                new Review(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),
                        false, 22, true, null, null),
                new Review(3, 1.3f, "No lo recomiendo", LocalDate.of(2023,2,1), false,
                        1, false, null, null)
        );

        List<ReviewOutDto> mockReviewOutDto = List.of(
                new ReviewOutDto(1,4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2), 35, true),
                new ReviewOutDto(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),22, true)
        );

        when(reviewRepository.findByRateGreaterThan(3f)).thenReturn(mockReviewList);
        when(modelMapper.map(mockReviewList, new TypeToken<List<ReviewOutDto>>() {}.getType())).thenReturn(mockReviewOutDto);

        List<ReviewOutDto> actualReviewList = reviewService.findAll("","",3f);
        assertEquals(2, actualReviewList.size());
        assertEquals(22, actualReviewList.getLast().getLikes());

        verify(reviewRepository, times(0)).findAll();
        verify(reviewRepository, times(1)).findByRateGreaterThan(3f);
        verify(reviewRepository, times(0)).findByUserUsername("");
        verify(reviewRepository, times(0)).findByEvent_Name("");
    }

    @Test
    public void testFindById() throws ReviewNotFoundException {
        Review mockReview = new Review(3, 4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2),
                true, 35, true, null, null);

        when(reviewRepository.findById(3L)).thenReturn(Optional.of(mockReview));

        Review review = reviewService.getReviewById(3L);
        assertEquals(35, review.getLikes());
        assertEquals(4.0f, review.getRate());

        verify(reviewRepository, times(1)).findById(3L);
    }

    @Test
    public void testFindByIdNotFoundReview() {

        when(reviewRepository.findById(2L)).thenReturn(Optional.empty());

        //Comprobamos que lanza la excepción
        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(2L));

        verify(reviewRepository, times(1)).findById(2L);
    }

    @Test
    public void testAddReview() {
        Event event = new Event();
        event.setId(20L);

        User user = new User();
        user.setId(3L);


        ReviewInDto reviewInDto = new ReviewInDto(LocalDate.of(2022,3,2), true, 4.0f, "Me ha gustado mucho",
                20, 3);

        Review registerReview = new Review(3, 4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2),
                true, 35, true, event, user);

        when(reviewRepository.save(any(Review.class))).thenReturn(registerReview);

        Review resultReview = reviewService.add(reviewInDto, event, user);

        assertEquals(registerReview, resultReview);
        assertTrue(resultReview.isRecommend());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    public void testModifyReview() throws ReviewNotFoundException {

        Event event = new Event();
        User user = new User();

        Review existingReview = new Review();
        existingReview.setRate(4.1f);
        existingReview.setId(45);

        ReviewModifyInDto updatingReview = new ReviewModifyInDto();
        updatingReview.setRate(3.5f);

        when(reviewRepository.findById(45L)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);

        reviewService.modify(45L, updatingReview, event, user);

        verify(modelMapper).map(updatingReview,existingReview);
        verify(reviewRepository).save(existingReview);
    }

    @Test
    public void testModifyReviewNotFound() {

        Event event = new Event();
        User user = new User();

        ReviewModifyInDto review = new ReviewModifyInDto();

        when(reviewRepository.findById(15L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.modify(15L, review, event, user));

        verify(reviewRepository, times(1)).findById(15L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void testDeleteReview() throws ReviewNotFoundException {
        Review review = new Review();
        review.setId(15L);

        when(reviewRepository.findById(15L)).thenReturn(Optional.of(review));

        reviewService.delete(15L);

        verify(reviewRepository, times(1)).findById(15L);
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    public void testDeleteReviewNotFound() {

        Review review = new Review();

        when(reviewRepository.findById(15L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.delete(15));

        verify(reviewRepository, times(1)).findById(15L);
        verify(reviewRepository, times(0)).delete(any(Review.class));
    }
}
