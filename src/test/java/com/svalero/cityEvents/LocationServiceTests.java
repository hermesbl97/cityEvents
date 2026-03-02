package com.svalero.cityEvents;

import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.LocationOutDto;
import com.svalero.cityEvents.exception.LocationNotFoundException;
import com.svalero.cityEvents.repository.LocationRepository;
import com.svalero.cityEvents.service.LocationService;
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
public class LocationServiceTests {

    @InjectMocks
    private LocationService locationService;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {

        List<Location> mockLocationList = List.of(
            new Location(1,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                  50009, LocalDate.of(2025,2,1), true, 61, -25, null),
            new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                    "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, 25, -84, null),
            new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                    "Independencia", 50002, LocalDate.of(2024,5,3), false, 43, 26, null)
        );

        List<LocationOutDto> mockLocationOutDto = List.of(
          new LocationOutDto(1,"La Romareda", "Estadio de fútbol", "Estadio", true),
          new LocationOutDto(2,"Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                  "Monumento", true),
          new LocationOutDto(3, "Teatro Principal","Teatro histórico de la ciudad", "Teatro", false)
        );

        when(locationRepository.findAll()).thenReturn(mockLocationList);
        when(modelMapper.map(mockLocationList, new TypeToken<List<LocationOutDto>>() {}.getType())).thenReturn(mockLocationOutDto);

        List<LocationOutDto> actualLocationList = locationService.findAll("",null,null);
        assertEquals(3, actualLocationList.size());
        assertEquals("Teatro", actualLocationList.getLast().getCategory());

        verify(locationRepository, times(1)).findAll();
        verify(locationRepository, times(0)).findByCategory("");
        verify(locationRepository, times(0)).findByDisabledAccessTrue();
        verify(locationRepository, times(0)).findByPostalCode(50001);
    }

    @Test
    public void testFindByCategory() {

        List<Location> mockLocationList = List.of(
                new Location(1,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                        50009, LocalDate.of(2025,2,1), true, 61, -25, null),
                new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, 25, -84, null),
                new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                        "Independencia", 50002, LocalDate.of(2024,5,3), false, 43, 26, null)
        );

        List<LocationOutDto> mockLocationOutDto = List.of(
                new LocationOutDto(2,"Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", true)
        );

        when(locationRepository.findByCategory("Monumento")).thenReturn(mockLocationList);
        when(modelMapper.map(mockLocationList, new TypeToken<List<LocationOutDto>>() {}.getType())).thenReturn(mockLocationOutDto);

        List<LocationOutDto> actualLocationList = locationService.findAll("Monumento",null,null);
        assertEquals(1, actualLocationList.size());
        assertEquals("Monumento", actualLocationList.getLast().getCategory());

        verify(locationRepository, times(0)).findAll();
        verify(locationRepository, times(1)).findByCategory("Monumento");
        verify(locationRepository, times(0)).findByDisabledAccessTrue();
        verify(locationRepository, times(0)).findByPostalCode(50001);
    }

    @Test
    public void testFindByPostalCode() {

        List<Location> mockLocationList = List.of(
                new Location(1,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                        50009, LocalDate.of(2025,2,1), true, 61, -25, null),
                new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, 25, -84, null),
                new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                        "Independencia", 50002, LocalDate.of(2024,5,3), false, 43, 26, null)
        );

        List<LocationOutDto> mockLocationOutDto = List.of(
                new LocationOutDto(2,"Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", true),
                new LocationOutDto(3, "Teatro Principal","Teatro histórico de la ciudad", "Teatro", false)
        );

        when(locationRepository.findByPostalCode(50001)).thenReturn(mockLocationList);
        when(modelMapper.map(mockLocationList, new TypeToken<List<LocationOutDto>>() {}.getType())).thenReturn(mockLocationOutDto);

        List<LocationOutDto> actualLocationList = locationService.findAll("",null,50001);
        assertEquals(2, actualLocationList.size());
        assertEquals("Teatro", actualLocationList.getLast().getCategory());

        verify(locationRepository, times(0)).findAll();
        verify(locationRepository, times(1)).findByPostalCode(50001);
        verify(locationRepository, times(0)).findByDisabledAccessTrue();
        verify(locationRepository, times(0)).findByCategory("");
    }

    @Test
    public void testFindByDiabledAccessTrue() {

        List<Location> mockLocationList = List.of(
                new Location(1,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                        50009, LocalDate.of(2025,2,1), true, 61, -25, null),
                new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, 25, -84, null),
                new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                        "Independencia", 50002, LocalDate.of(2024,5,3), false, 43, 26, null)
        );

        List<LocationOutDto> mockLocationOutDto = List.of(
                new LocationOutDto(1,"La Romareda", "Estadio de fútbol", "Estadio", true),
                new LocationOutDto(2,"Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", true)
        );

        when(locationRepository.findByDisabledAccessTrue()).thenReturn(mockLocationList);
        when(modelMapper.map(mockLocationList, new TypeToken<List<LocationOutDto>>() {}.getType())).thenReturn(mockLocationOutDto);

        List<LocationOutDto> actualLocationList = locationService.findAll("",true,null);
        assertEquals(2, actualLocationList.size());
        assertEquals("Monumento", actualLocationList.getLast().getCategory());

        verify(locationRepository, times(0)).findAll();
        verify(locationRepository, times(1)).findByDisabledAccessTrue();
        verify(locationRepository, times(0)).findByCategory("");
        verify(locationRepository, times(0)).findByPostalCode(50001);
    }

    @Test
    public void testFindLocationById() throws LocationNotFoundException {
        Location mockLocation = new Location(15,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                50009, LocalDate.of(2025,2,1), true, 26, 21, null);


        when(locationRepository.findById(15L)).thenReturn(Optional.of(mockLocation));

        Location location = locationService.findById(15L);
        assertEquals("Isabel la Católica", location.getStreetLocated());
        assertTrue(location.isDisabledAccess());

        verify(locationRepository, times(1)).findById(15L);
    }

    @Test
    public void testFindLocationByIdNotFound() throws LocationNotFoundException {

        when(locationRepository.findById(65L)).thenReturn(Optional.empty());

        assertThrows(LocationNotFoundException.class, () -> locationService.findById(65L));

        verify(locationRepository, times(1)).findById(65L);
    }

    @Test
    public void testAddLocation() {
        Location registerLocation = new Location(15,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                50009, LocalDate.of(2025,2,1), true, 26, 21, null);

        when(locationRepository.save(any(Location.class))).thenReturn(registerLocation);

        Location location = locationService.add(registerLocation);

        assertEquals(registerLocation, location);
        assertEquals("La Romareda", location.getName());
        assertEquals("Estadio", location.getCategory());

        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    public void testModifyLocation() throws LocationNotFoundException {

        Location existingLocation = new Location();
        existingLocation.setCategory("Monumento");
        existingLocation.setId(4);

        Location updatingLocation = new Location();
        updatingLocation.setCategory("Cine");

        when(locationRepository.findById(4L)).thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(existingLocation)).thenReturn(existingLocation);

        locationService.modify(4L, updatingLocation);

        verify(modelMapper).map(updatingLocation,existingLocation);
        verify(locationRepository).save(existingLocation);
    }

    @Test
    public void testModifyLcoationNotFound() {

        Location location = new Location();

        when(locationRepository.findById(21L)).thenReturn(Optional.empty());

        assertThrows(LocationNotFoundException.class, () -> locationService.modify(21L, location));


        verify(locationRepository, times(1)).findById(21L);
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    public void testDeleteLocation() throws LocationNotFoundException {

        Location location = new Location();
        location.setId(15L);

        when(locationRepository.findById(15L)).thenReturn(Optional.of(location));

        locationService.delete(15L);

        verify(locationRepository, times(1)).findById(15L);
        verify(locationRepository, times(1)).delete(location);
    }

    @Test
    public void testDeleteLocationNotFound() {

        when(locationRepository.findById(19L)).thenReturn(Optional.empty());

        assertThrows(LocationNotFoundException.class, () -> locationService.delete(19));

        verify(locationRepository, times(1)).findById(19L);
        verify(locationRepository, times(0)).delete(any(Location.class));
    }

}
