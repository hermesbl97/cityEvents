package com.svalero.cityEvents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.cityEvents.controller.EventController;
import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.EventInDto;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.exception.LocationNotFoundException;
import com.svalero.cityEvents.service.ArtistService;
import com.svalero.cityEvents.service.EventService;
import com.svalero.cityEvents.service.LocationService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTests {

    @Autowired
    private MockMvc mockMvc; //permite hacer llamadas HTTP como si fuera Postman

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private ArtistService artistService;

    @MockitoBean
    private LocationService locationService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;  //convierte de String a java

    @Test
    public void testGetAll() throws Exception {
        List<EventOutDto> eventsOutDto = List.of(
            new EventOutDto(1,"Campanadas Año nuevo", LocalDate.now(), "Evento", 32),
            new EventOutDto(2, "Final Copa Casademont", LocalDate.now(), "Deporte", 25),
            new EventOutDto(3, "Panorama desde el Puente", LocalDate.of(2025,1,2), "Evento", 25)
        );

        when(eventService.findAll(null,null,null)).thenReturn(eventsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(3, eventsListResponse.size());
        assertEquals("Panorama desde el Puente", eventsListResponse.getLast().getName());
    }

    @Test
    public void testGetAllByCategory() throws Exception {
        List<EventOutDto> eventsOutDto = List.of(
                new EventOutDto(1,"Campanadas Año nuevo", LocalDate.now(), "Evento", 32),
                new EventOutDto(3, "Panorama desde el Puente", LocalDate.of(2025,1,2), "Evento", 25)
        );

        when(eventService.findAll("Evento",null,null)).thenReturn(eventsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .queryParam("category", "Evento")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, eventsListResponse.size());
        assertEquals("Panorama desde el Puente", eventsListResponse.getLast().getName());
    }

    @Test
    public void testGetAllByPriceLessThanEqualOrderByPriceAsc() throws Exception {
        List<EventOutDto> eventsOutDto = List.of(
                new EventOutDto(2,"Final Copa Casademont", LocalDate.now(), "Deporte", 25),
                new EventOutDto(3,"Panorama desde el Puente", LocalDate.of(2025,1,2), "Evento",25)
        );

        when(eventService.findAll(null,null,30f)).thenReturn(eventsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .queryParam("price", "30f")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, eventsListResponse.size());
        assertEquals("Final Copa Casademont", eventsListResponse.getFirst().getName());
    }

    @Test
    public void testGetAllByLocation_Name() throws Exception {

        List<EventOutDto> eventsOutDto = List.of(
                new EventOutDto(3,"Panorama desde el Puente", LocalDate.of(2025,1,2), "Evento",25)
        );

        when(eventService.findAll(null,"Plaza del Pilar",null)).thenReturn(eventsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .queryParam("locationName", "Plaza del Pilar")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, eventsListResponse.size());
        assertEquals("Panorama desde el Puente", eventsListResponse.getFirst().getName());
    }

    @Test
    public void testAddEvent() throws Exception {

        Location location = new Location(1,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                50009, LocalDate.of(2025,2,1), true, null);

        List<Artist> artists = List.of(
                new Artist(1,"Jesús", "García", "Masculino", LocalDate.of(1999,3,2), "Cantante",
                        2523697, 1.77F, true, null),
                new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                        "Actor", 2645987, 1.68F, false, null)
        );

        EventInDto eventInDto = new EventInDto("Ofrenda de Flores", "Acto ceremonioso", LocalDate.of(2026,10,12),
                "Ceremonia", 15000,0,1,List.of(1L,2L));

        Event newEvent = new Event();

        when(locationService.findById(1L)).thenReturn(location);
        when(artistService.findAllArtistsById(List.of(1L,2L))).thenReturn(artists);
        when(eventService.add(location,eventInDto,artists)).thenReturn(newEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventInDto)))
                        .andExpect(status().isCreated())
                        .andReturn();

        verify(locationService, times(1)).findById(1L);
        verify(artistService, times(1)).findAllArtistsById(List.of(1L,2L));
        verify(eventService, times(1)).add(location,eventInDto,artists);
    }

    @Test
    public void testAddEventValidationError400() throws Exception {
        EventInDto notValidEvent = new EventInDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidEvent)))
                        .andExpect(status().isBadRequest())
                        .andReturn();

        verifyNoInteractions(locationService);
        verifyNoInteractions(artistService);
        verifyNoInteractions(eventService);
    }

    @Test
    public void testAddEventLocationNotFound404() throws Exception {
        EventInDto eventInDto = new EventInDto("Campanadas Año nuevo", "Un año más las campanadas tendrán lugar en la Plaza del pilar",
                LocalDate.of(2025,1,2), "Evento", 2500, 32, 99L, //no existe una localización con este Id
                List.of(1L,2L));

        when(locationService.findById(99L)).thenThrow(new LocationNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventInDto)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(locationService, times(1)).findById(99L);
        verifyNoInteractions(artistService);
        verifyNoInteractions(eventService);
    }

    @Test
    public void testGetEventById() throws Exception {
        Event event = new Event(2, "Final Copa Casademont", "Partido femenino en el que el Caseademont Zaragoza puede ganar",
                LocalDate.now(), "Deporte", 12000, 25, true, null, null, null);

        when(eventService.findById(2L)).thenReturn(event);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/events/2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Event eventResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("Final Copa Casademont", eventResponse.getName());
        verify(eventService, times(1)).findById(2L);
    }

    @Test
    public void testGetEventByIdNotFound() throws Exception {
        when(eventService.findById(99L)).thenThrow(new EventNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/events/99")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(eventService, times(1)).findById(99L);
    }

    @Test
    public void testModifyEvent() throws Exception {
        Event eventRequest = new Event();
        eventRequest.setName("Evento");

        Event eventResponse = new Event();
        eventResponse.setId(1L);
        eventResponse.setName("Evento actualizado");

        when(eventService.modify(eq(1L), any(Event.class))).thenReturn(eventResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Event response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("Evento actualizado", response.getName());
        verify(eventService, times(1)).modify(eq(1L),any(Event.class));
    }

    @Test
    public void testModifyEventNotFound() throws Exception {
        Event eventRequest = new Event();
        eventRequest.setName("Evento inexistente");

        when(eventService.modify(eq(2L), any(Event.class))).thenThrow(new EventNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/events/2")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                        .andExpect(status().isNotFound());

        verify(eventService, times(1)).modify(eq(2L), any(Event.class));
    }

    @Test
    public void testDeleteEvent() throws Exception {
        doNothing().when(eventService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/events/1"))
                        .andExpect(status().isNoContent());

        verify(eventService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteEventNotFound() throws Exception {

        doThrow(new EventNotFoundException()).when(eventService).delete(2L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/events/2"))
                        .andExpect(status().isNotFound());

        verify(eventService, times(1)).delete(2L);
    }

}
