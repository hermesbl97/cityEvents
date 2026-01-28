package com.svalero.cityEvents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.cityEvents.controller.ArtistController;
import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.ArtistOutDto;
import com.svalero.cityEvents.dto.EventInDto;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.exception.ArtistNotFoundException;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.service.ArtistService;
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

@WebMvcTest(ArtistController.class)
public class ArtistControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistService artistService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAll() throws Exception {

        List<ArtistOutDto> artistOutDto = List.of(
                new ArtistOutDto(2,"María", "Martinez", 2645987, "Actor"),
                new ArtistOutDto(1,"Jesús","García",2523697,"Cantante"),
                new ArtistOutDto(3, "Carla", "Gallardo", 789231, "Deportista")
        );

        when(artistService.findAll(null,null,null)).thenReturn(artistOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/artists")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ArtistOutDto> artistsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(3, artistsListResponse.size());
        assertEquals("Carla", artistsListResponse.getLast().getName());
    }

    @Test
    public void testGetAllByType() throws Exception {
        List<ArtistOutDto> artistsOutDto = List.of(
                new ArtistOutDto(3, "Carla", "Gallardo", 789231, "Deportista")
        );

        when(artistService.findAll("Deportista",null,null)).thenReturn(artistsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/artists")
                        .queryParam("type", "Deportista")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ArtistOutDto> artistsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, artistsListResponse.size());
        assertEquals("Carla", artistsListResponse.getLast().getName());
    }

    @Test
    public void testGetAllByActiveTrue() throws Exception {
        List<ArtistOutDto> artistsOutDto = List.of(
                new ArtistOutDto(2,"María", "Martinez", 2645987, "Actor"),
                new ArtistOutDto(1,"Jesús","García",2523697,"Cantante")
        );

        when(artistService.findAll(null,true,null)).thenReturn(artistsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/artists")
                        .queryParam("active", "true")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ArtistOutDto> artistsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, artistsListResponse.size());
        assertEquals("Jesús", artistsListResponse.getLast().getName());
    }

    @Test
    public void testGetAllOrderByFollowersDesc() throws Exception {

        List<ArtistOutDto> artistsOutDto = List.of(
                new ArtistOutDto(2,"María", "Martinez", 2645987, "Actor"),
                new ArtistOutDto(1,"Jesús","García",2523697,"Cantante"),
                new ArtistOutDto(3, "Carla", "Gallardo", 789231, "Deportista")
        );

        when(artistService.findAll(null,null,true)).thenReturn(artistsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/artists")
                        .queryParam("orderByFollowers", "true")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ArtistOutDto> artistsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(3, artistsListResponse.size());
        assertEquals("María", artistsListResponse.getFirst().getName());
    }

    @Test
    public void testAddArtist() throws Exception {

        Artist artist = new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                "Actor", 2645987, 1.68F, false, null);

        Artist newArtist = new Artist();

        when(artistService.add(artist)).thenReturn(newArtist);

        mockMvc.perform(MockMvcRequestBuilders.post("/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artist)))
                        .andExpect(status().isCreated())
                        .andReturn();

        verify(artistService, times(1)).add(artist);
    }

    @Test
    public void testAddArtistValidationError400() throws Exception {
        Artist notValidArtist = new Artist();

        mockMvc.perform(MockMvcRequestBuilders.post("/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidArtist)))
                        .andExpect(status().isBadRequest())
                        .andReturn();

        verifyNoInteractions(artistService);
    }

    @Test
    public void testGetArtistById() throws Exception {

        Artist artist = new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                "Actor", 2645987, 1.68F, false, null);

        when(artistService.findArtistById(2L)).thenReturn(artist);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/artists/2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Artist artistResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("María", artistResponse.getName());
        verify(artistService, times(1)).findArtistById(2L);
    }

    @Test
    public void testGetArtistByIdNotFound() throws Exception {
        when(artistService.findArtistById(99L)).thenThrow(new ArtistNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/artists/99")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(artistService, times(1)).findArtistById(99L);
    }

    @Test
    public void testModifyArtist() throws Exception {
        Artist artistRequest = new Artist();
        artistRequest.setName("Carlos");

        Artist artistResponse = new Artist();
        artistResponse.setId(1L);
        artistResponse.setName("Jorge");

        when(artistService.modify(eq(1L), any(Artist.class))).thenReturn(artistResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/artists/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(artistRequest)))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Artist response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("Jorge", response.getName());
        verify(artistService, times(1)).modify(eq(1L),any(Artist.class));
    }

    @Test
    public void testModifyArtistNotFound() throws Exception {

        Artist artistRequest = new Artist();
        artistRequest.setName("Carlos");

        when(artistService.modify(eq(2L), any(Artist.class))).thenThrow(new ArtistNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/artists/2")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(artistRequest)))
                        .andExpect(status().isNotFound());

        verify(artistService, times(1)).modify(eq(2L), any(Artist.class));
    }

    @Test
    public void testDeleteArtist() throws Exception {
        doNothing().when(artistService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/artists/1"))
                .andExpect(status().isNoContent());

        verify(artistService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteArtistNotFound() throws Exception {

        doThrow(new ArtistNotFoundException()).when(artistService).delete(2L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/artists/2"))
                .andExpect(status().isNotFound());

        verify(artistService, times(1)).delete(2L);
    }
}
