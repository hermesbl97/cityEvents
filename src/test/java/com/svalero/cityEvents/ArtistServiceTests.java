package com.svalero.cityEvents;

import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.dto.ArtistOutDto;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.repository.ArtistRepository;
import com.svalero.cityEvents.service.ArtistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTests {

    @InjectMocks
    private ArtistService artistService;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Artist> mockArtistList = List.of(
          new Artist(1,"Jesús", "García", "Masculino", LocalDate.of(1999,3,2), "Cantante",
                  2523697, 1.77F, true, null),
            new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                    "Actor", 2645987, 1.68F, false, null),
            new Artist(3, "Carla", "Gallardo", "Femenino", LocalDate.of(1997,8,6), "Deportista",
                    789231, 1.65F, true, null)
        );

        List<ArtistOutDto> mockArtistOutDto = List.of(
          new ArtistOutDto(1,"Jesús","García",2523697,"Cantante"),
          new ArtistOutDto(2,"María", "Martinez", 2645987, "Actor"),
          new ArtistOutDto(3, "Carla", "Gallardo", 789231, "Deportista")
        );

        when(artistRepository.findAll()).thenReturn(mockArtistList);
        when(modelMapper.map(mockArtistList, new TypeToken<List<ArtistOutDto>>() {}.getType())).thenReturn(mockArtistOutDto);

        List<ArtistOutDto> actualArtistList = artistService.findAll("",null, null);
        assertEquals(3, actualArtistList.size());
        assertEquals("Deportista", actualArtistList.getLast().getType());

        verify(artistRepository, times(1)).findAll();
        verify(artistRepository, times(0)).findByType("");
    }

    @Test
    public void testFindByType() {
        List<Artist> mockArtistList = List.of(
                new Artist(1,"Jesús", "García", "Masculino", LocalDate.of(1999,3,2), "Cantante",
                        2523697, 1.77F, true, null),
                new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                        "Actor", 2645987, 1.68F, false, null),
                new Artist(3, "Carla", "Gallardo", "Femenino", LocalDate.of(1997,8,6), "Deportista",
                        789231, 1.65F, true, null)
        );

        List<ArtistOutDto> mockArtistOutDto = List.of(
                new ArtistOutDto(2,"María", "Martinez", 2645987, "Actor")
        );

        when(artistRepository.findByType("Actor")).thenReturn(mockArtistList);
        when(modelMapper.map(mockArtistList, new TypeToken<List<ArtistOutDto>>() {}.getType())).thenReturn(mockArtistOutDto);

        List<ArtistOutDto> actualArtistList = artistService.findAll("Actor",null, null);
        assertEquals(1, actualArtistList.size());
        assertEquals("Actor", actualArtistList.getLast().getType());

        verify(artistRepository, times(0)).findAll();
        verify(artistRepository, times(1)).findByType("Actor");
    }

    @Test
    public void testFindByActiveTrue() {
        List<Artist> mockArtistList = List.of(
                new Artist(1,"Jesús", "García", "Masculino", LocalDate.of(1999,3,2), "Cantante",
                        2523697, 1.77F, true, null),
                new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                        "Actor", 2645987, 1.68F, false, null),
                new Artist(3, "Carla", "Gallardo", "Femenino", LocalDate.of(1997,8,6), "Deportista",
                        789231, 1.65F, true, null)
        );

        List<ArtistOutDto> mockArtistOutDto = List.of(
                new ArtistOutDto(1,"Jesús","García",2523697,"Cantante"),
                new ArtistOutDto(3, "Carla", "Gallardo", 789231, "Deportista")
        );

        when(artistRepository.findByActiveTrue()).thenReturn(mockArtistList);
        when(modelMapper.map(mockArtistList, new TypeToken<List<ArtistOutDto>>() {}.getType())).thenReturn(mockArtistOutDto);

        List<ArtistOutDto> actualArtistList = artistService.findAll("",true, null);
        assertEquals(2, actualArtistList.size());
        assertEquals("Cantante", actualArtistList.getFirst().getType());

        verify(artistRepository, times(0)).findAll();
        verify(artistRepository, times(1)).findByActiveTrue();
    }

    @Test
    public void testFindAllByOrderByFollowersDesc() {
        List<Artist> mockArtistList = List.of(
                new Artist(1,"Jesús", "García", "Masculino", LocalDate.of(1999,3,2), "Cantante",
                        2523697, 1.77F, true, null),
                new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                        "Actor", 2645987, 1.68F, false, null),
                new Artist(3, "Carla", "Gallardo", "Femenino", LocalDate.of(1997,8,6), "Deportista",
                        789231, 1.65F, true, null)
        );

        List<ArtistOutDto> mockArtistOutDto = List.of(
                new ArtistOutDto(2,"María", "Martinez", 2645987, "Actor"),
                new ArtistOutDto(1,"Jesús","García",2523697,"Cantante"),
                new ArtistOutDto(3, "Carla", "Gallardo", 789231, "Deportista")
        );

        when(artistRepository.findAllByOrderByFollowersDesc()).thenReturn(mockArtistList);
        when(modelMapper.map(mockArtistList, new TypeToken<List<ArtistOutDto>>() {}.getType())).thenReturn(mockArtistOutDto);

        List<ArtistOutDto> actualArtistList = artistService.findAll("",null, true);
        assertEquals(3, actualArtistList.size());
        assertEquals("Actor", actualArtistList.getFirst().getType());

        verify(artistRepository, times(0)).findAll();
        verify(artistRepository, times(1)).findAllByOrderByFollowersDesc();
    }



}
