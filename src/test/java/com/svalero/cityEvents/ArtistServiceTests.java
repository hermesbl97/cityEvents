package com.svalero.cityEvents;

import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.dto.ArtistOutDto;
import com.svalero.cityEvents.exception.ArtistNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        verify(artistRepository, times(0)).findAllByOrderByFollowersDesc();
        verify(artistRepository, times(0)).findByActiveTrue();
    }

    @Test
    public void testFindByType() {

        List<Artist> mockArtistList = List.of(
                new Artist(1,"Jesús", "García", "Masculino", LocalDate.of(1999,3,2), "Cantante",
                        2523697, 1.77F, true,null),
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
        verify(artistRepository, times(0)).findAllByOrderByFollowersDesc();
        verify(artistRepository, times(0)).findByActiveTrue();
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
        verify(artistRepository, times(0)).findAllByOrderByFollowersDesc();
        verify(artistRepository, times(0)).findByType("");
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
        verify(artistRepository, times(0)).findByType("");
        verify(artistRepository, times(0)).findByActiveTrue();
    }

    @Test
    public void testFindArtistById() throws ArtistNotFoundException {
        Artist mockArtist = new Artist(8,"Jesús", "García", "Masculino", LocalDate.of(1999,3,2), "Cantante",
                2523697, 1.77F, true, null);


        when(artistRepository.findById(8L)).thenReturn(Optional.of(mockArtist));

        Artist artist = artistService.findArtistById(8L);
        assertEquals("García", artist.getSurname());
        assertEquals(1.77F, artist.getHeight());

        verify(artistRepository, times(1)).findById(8L);
    }

    @Test
    public void testFindArtistByIdNotFound() throws ArtistNotFoundException {

        when(artistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> artistService.findArtistById(99L));

        verify(artistRepository, times(1)).findById(99L);
    }

    @Test
    public void testFindAllArtistsById() {

        List<Artist> mockArtists = List.of(
                new Artist(1,"Jesús", "García", "Masculino", LocalDate.of(1999,3,2), "Cantante",
                        2523697, 1.77F, true, null),
                new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                        "Actor", 2645987, 1.68F, false, null)
        );

        when(artistRepository.findAllById(List.of(1L,2L))).thenReturn(mockArtists);

        List<Artist> artistList = artistService.findAllArtistsById(List.of(1L,2L));

        assertEquals(2, artistList.size());
        assertEquals("María", artistList.getLast().getName());
        assertEquals("Masculino", artistList.getFirst().getGenre());

        verify(artistRepository, times(1)).findAllById(List.of(1L,2L));
    }

    @Test
    public void testAddArtist() {
        Artist registerArtist = new Artist(2, "María", "Martinez", "Femenino", LocalDate.of(2000,5,17),
                "Actor", 2645987, 1.68F, false,  null);


        when(artistRepository.save(any(Artist.class))).thenReturn(registerArtist);

        Artist artist = artistService.add(registerArtist);

        assertEquals(registerArtist, artist);
        assertEquals("María", artist.getName());
        assertEquals("Actor", artist.getType());

        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    public void testModifyArtist() throws ArtistNotFoundException {

        Artist existingArtist = new Artist();
        existingArtist.setName("Carolina");
        existingArtist.setId(4);

        Artist updatingArtist = new Artist();
        updatingArtist.setName("Marta");

        when(artistRepository.findById(4L)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(existingArtist)).thenReturn(existingArtist);

        artistService.modify(4L, updatingArtist);

        verify(modelMapper).map(updatingArtist,existingArtist);
        verify(artistRepository, times(1)).findById(4L);
        verify(artistRepository).save(existingArtist);
    }

    @Test
    public void testModifyArtistNotFound() {

        Artist artist = new Artist();

        when(artistRepository.findById(15L)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> artistService.modify(15L, artist));


        verify(artistRepository, times(1)).findById(15L);
        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    public void testDeleteArtist() throws ArtistNotFoundException {

        Artist artist = new Artist();
        artist.setId(15L);

        when(artistRepository.findById(15L)).thenReturn(Optional.of(artist));

        artistService.delete(15L);

        verify(artistRepository, times(1)).findById(15L);
        verify(artistRepository, times(1)).delete(artist);
    }

    @Test
    public void testDeleteArtistNotFound() {

        Artist artist = new Artist();

        when(artistRepository.findById(19L)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> artistService.delete(19));


        verify(artistRepository, times(1)).findById(19L);
        verify(artistRepository, times(0)).delete(any(Artist.class));
    }
}
