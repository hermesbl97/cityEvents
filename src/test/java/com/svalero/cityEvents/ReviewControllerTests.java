package com.svalero.cityEvents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.cityEvents.controller.ReviewController;
import com.svalero.cityEvents.domain.*;
import com.svalero.cityEvents.dto.*;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.exception.LocationNotFoundException;
import com.svalero.cityEvents.exception.ReviewNotFoundException;
import com.svalero.cityEvents.exception.UserNotFoundException;
import com.svalero.cityEvents.service.EventService;
import com.svalero.cityEvents.service.ReviewService;
import com.svalero.cityEvents.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private UserService userService;

    @Test
    public void testGetAll() throws Exception {
        List<ReviewOutDto> reviewsOutDto = List.of(
                new ReviewOutDto(1,4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2), 35, true),
                new ReviewOutDto(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),22, true),
                new ReviewOutDto(3, 1.3f, "No lo recomiendo", LocalDate.of(2023,2,1),1, false)
        );

        when(reviewService.findAll(null,null,null)).thenReturn(reviewsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/reviews")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ReviewOutDto> reviewsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(3, reviewsListResponse.size());
        assertEquals(1.3f, reviewsListResponse.getLast().getRate());
    }

    @Test
    public void testGetAllByRate() throws Exception {
        List<ReviewOutDto> reviewsOutDto = List.of(
                new ReviewOutDto(1,4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2), 35, true),
                new ReviewOutDto(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),22, true)
        );

        when(reviewService.findAll(null,null,3f)).thenReturn(reviewsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/reviews")
                        .queryParam("rate", "3.0")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ReviewOutDto> reviewsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, reviewsListResponse.size());
        assertEquals(3.2f, reviewsListResponse.getLast().getRate());
    }

    @Test
    public void testGetAllByUsername() throws Exception {

        List<ReviewOutDto> reviewsOutDto = List.of(
                new ReviewOutDto(1,4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2), 35, true)
        );

        when(reviewService.findAll("pedro123",null,null)).thenReturn(reviewsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/reviews")
                        .queryParam("username", "pedro123")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ReviewOutDto> reviewsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, reviewsListResponse.size());
        assertEquals(4.0f, reviewsListResponse.getFirst().getRate());
    }

    @Test
    public void testGetAllByEventName() throws Exception {

        List<ReviewOutDto> reviewsOutDto = List.of(
                new ReviewOutDto(1,4.0f, "Me ha gustado mucho", LocalDate.of(2022,3,2), 35, true)
        );

        when(reviewService.findAll(null,"Conicerto Kase.O",null)).thenReturn(reviewsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/reviews")
                        .queryParam("eventName", "Conicerto Kase.O")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ReviewOutDto> reviewsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, reviewsListResponse.size());
        assertEquals(4.0f, reviewsListResponse.getFirst().getRate());
    }

    @Test
    public void testGetReviewById() throws Exception {
        Review review = new Review(2, 3.2f, "Ha estado bien, pero mejorable", LocalDate.of(2023,5,12),
                false, 22, true, null, null);

        when(reviewService.getReviewById(2L)).thenReturn(review);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/reviews/2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Review reviewResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(22, reviewResponse.getLikes());
        verify(reviewService, times(1)).getReviewById(2L);
    }

    @Test
    public void testGetReviewtByIdNotFound() throws Exception {
        when(reviewService.getReviewById(99L)).thenThrow(new ReviewNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/reviews/99")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(reviewService, times(1)).getReviewById(99L);
    }

    @Test
    public void testAddReview() throws Exception {

        Event event = new Event(1,"Campanadas Año nuevo", "Un año más las campanadas tendrán lugar en la Plaza del pilar",
                LocalDate.now(), "Evento", 2500, 32, true, null, null, null );

        User user = new User(5, "martin123", "Jorge", "Ruiz", LocalDate.of(1987,2,5), 615987325, true, null);

        ReviewInDto reviewInDto = new ReviewInDto(LocalDate.of(2025,3,2), true, 4.1f, "Me ha encantado",
                1,5);

        Review newReview = new Review();

        when(eventService.findById(1L)).thenReturn(event);
        when(userService.findUserById(5)).thenReturn(user);
        when(reviewService.add(reviewInDto, event, user)).thenReturn(newReview);

        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewInDto)))
                        .andExpect(status().isCreated())
                        .andReturn();

        verify(eventService, times(1)).findById(1L);
        verify(userService, times(1)).findUserById(5L);
        verify(reviewService, times(1)).add(reviewInDto, event, user);
    }

    @Test
    public void testAddReviewValidationError400() throws Exception {
        ReviewInDto notValidReview = new ReviewInDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidReview)))
                        .andExpect(status().isBadRequest())
                        .andReturn();

        verifyNoInteractions(userService);
        verifyNoInteractions(eventService);
        verifyNoInteractions(reviewService);
    }

    @Test
    public void testAddReviewEventNotFound404() throws Exception {

        ReviewInDto reviewInDto = new ReviewInDto(LocalDate.of(2025,3,2), true, 4.1f, "Me ha encantado",
                1,5);

        when(eventService.findById(1L)).thenThrow(new EventNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewInDto)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(eventService, times(1)).findById(1L);
        verifyNoInteractions(userService);
        verifyNoInteractions(reviewService);
    }

    @Test
    public void testAddReviewUserNotFound404() throws Exception {
        ReviewInDto reviewInDto = new ReviewInDto(LocalDate.of(2025,3,2), true, 4.1f, "Me ha encantado",
                1,5);

        Event event = new Event();

        when(eventService.findById(1L)).thenReturn(event);
        when(userService.findUserById(5L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewInDto)))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(eventService, times(1)).findById(1L);
        verify(userService, times(1)).findUserById(5L);
        verifyNoInteractions(reviewService);
    }

    @Test
    public void testModifyReview() throws Exception {

        User user = new User();
        Event event = new Event();

        ReviewModifyInDto reviewRequest = new ReviewModifyInDto();
        reviewRequest.setRate(3.2f);
        reviewRequest.setUserId(1L);
        reviewRequest.setEventId(2L);

        Review reviewResponse = new Review();
        reviewResponse.setId(15L);
        reviewResponse.setRate(4.0f);

        when(reviewService.modify(eq(15L), any(ReviewModifyInDto.class), any(Event.class), any(User.class)))
                .thenReturn(reviewResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/reviews/15")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Review response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(4.0f, response.getRate());
        verify(reviewService, times(1)).modify(eq(15L),any(ReviewModifyInDto.class), any(Event.class), any(User.class));
    }

    @Test
    public void testModifyReviewNotFound() throws Exception {
        ReviewModifyInDto reviewRequest = new ReviewModifyInDto();
        reviewRequest.setRate(3.8f);
        reviewRequest.setUserId(1L);
        reviewRequest.setEventId(2L);

        when(reviewService.modify(eq(15L), any(ReviewModifyInDto.class), any(Event.class), any(User.class)))
                .thenThrow(new ReviewNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/reviews/15")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                        .andExpect(status().isNotFound());

        verify(reviewService, times(1)).modify(eq(15L), any(ReviewModifyInDto.class), any(Event.class), any(User.class));
    }

    @Test
    public void testDeleteReview() throws Exception {
        doNothing().when(reviewService).delete(17L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/17"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).delete(17L);
    }

    @Test
    public void testDeleteReviewNotFound() throws Exception {

        doThrow(new ReviewNotFoundException()).when(reviewService).delete(17L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/17"))
                .andExpect(status().isNotFound());

        verify(reviewService, times(1)).delete(17L);
    }
}
