package com.svalero.cityEvents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.cityEvents.controller.LocationController;
import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.EventInDto;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.dto.LocationOutDto;
import com.svalero.cityEvents.exception.ArtistNotFoundException;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.exception.LocationNotFoundException;
import com.svalero.cityEvents.service.LocationService;
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

@WebMvcTest(LocationController.class)
public class LocationControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private LocationService locationService;


    @Test
    public void testGetAll() throws Exception {
        List<LocationOutDto> locationOutDto = List.of(
                new LocationOutDto(1,"La Romareda", "Estadio de fútbol", "Estadio", true),
                new LocationOutDto(2,"Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", true),
                new LocationOutDto(3, "Teatro Principal","Teatro histórico de la ciudad", "Teatro", false)
        );

        when(locationService.findAll(null,null,null)).thenReturn(locationOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/locations")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<LocationOutDto> locationsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(3, locationsListResponse.size());
        assertEquals("Teatro Principal", locationsListResponse.getLast().getName());
    }

    @Test
    public void testGetAllByCategory() throws Exception {
        List<LocationOutDto> locationsOutDto = List.of(
                new LocationOutDto(1,"La Romareda", "Estadio de fútbol", "Estadio", true)
        );

        when(locationService.findAll("Estadio",null,null)).thenReturn(locationsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/locations")
                        .queryParam("category", "Estadio")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<LocationOutDto> locationsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, locationsListResponse.size());
        assertEquals("La Romareda", locationsListResponse.getLast().getName());
    }

    @Test
    public void testGetAllByPostalCode() throws Exception {
        List<LocationOutDto> locationsOutDto = List.of(
                new LocationOutDto(1,"La Romareda", "Estadio de fútbol", "Estadio", true),
                new LocationOutDto(3, "Teatro Principal","Teatro histórico de la ciudad", "Teatro", false)
        ); //suponemos que ambos tienen el mismo código postal

        when(locationService.findAll(null,null,50001)).thenReturn(locationsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/locations")
                        .queryParam("postalCode", "50001")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<LocationOutDto> locationsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, locationsListResponse.size());
        assertEquals("Teatro", locationsListResponse.getLast().getCategory());
    }

    @Test
    public void testGetAllByDisabledAccess() throws Exception {
        List<LocationOutDto> locationsOutDto = List.of(
                new LocationOutDto(1,"La Romareda", "Estadio de fútbol", "Estadio", true),
                new LocationOutDto(2,"Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                "Monumento", true)
        );

        when(locationService.findAll(null,true,null)).thenReturn(locationsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/locations")
                        .queryParam("disabledAccess", "true")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<LocationOutDto> locationsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, locationsListResponse.size());
        assertEquals("Monumento", locationsListResponse.getLast().getCategory());
    }

    @Test
    public void testGetLocationById() throws Exception {
        Location location = new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                "Independencia", 50001, LocalDate.of(2024,5,3), false, 36, 98, null);

        when(locationService.findById(3L)).thenReturn(location);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/locations/3")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Location locationResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("Teatro Principal", locationResponse.getName());
        verify(locationService, times(1)).findById(3L);
    }

    @Test
    public void testGetLocationByIdNotFound() throws Exception {
        when(locationService.findById(99L)).thenThrow(new LocationNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/locations/99")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(locationService, times(1)).findById(99L);
    }

    @Test
    public void testAddLocation() throws Exception {

        Location location = new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, 15, 24, null);

        Location newLocation = new Location();

        when(locationService.add(location)).thenReturn(newLocation);

        mockMvc.perform(MockMvcRequestBuilders.post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location)))
                        .andExpect(status().isCreated())
                        .andReturn();

        verify(locationService, times(1)).add(location);
    }

    @Test
    public void testAddLocationValidationError400() throws Exception {
        Location notValidLocation = new Location();

        mockMvc.perform(MockMvcRequestBuilders.post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidLocation)))
                        .andExpect(status().isBadRequest())
                        .andReturn();

        verifyNoInteractions(locationService);
    }

    @Test
    public void testModifyLocation() throws Exception {
        Location locationRequest = new Location();
        locationRequest.setName("Teatro Principal");

        Location locationResponse = new Location();
        locationResponse.setId(1L);
        locationResponse.setName("Museo Goya");

        when(locationService.modify(eq(1L), any(Location.class))).thenReturn(locationResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/locations/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Location response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("Museo Goya", response.getName());
        verify(locationService, times(1)).modify(eq(1L),any(Location.class));
    }

    @Test
    public void testModifyLocationNotFound() throws Exception {

        Location locationRequest = new Location();
        locationRequest.setName("Museo Goya");

        when(locationService.modify(eq(10L), any(Location.class))).thenThrow(new LocationNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/locations/10")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                        .andExpect(status().isNotFound());

        verify(locationService, times(1)).modify(eq(10L), any(Location.class));
    }

    @Test
    public void testDeleteLocation() throws Exception {
        doNothing().when(locationService).delete(5L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/locations/5"))
                .andExpect(status().isNoContent());

        verify(locationService, times(1)).delete(5L);
    }

    @Test
    public void testDeleteLocationNotFound() throws Exception {

        doThrow(new LocationNotFoundException()).when(locationService).delete(5L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/locations/5"))
                .andExpect(status().isNotFound());

        verify(locationService, times(1)).delete(5L);
    }
}
