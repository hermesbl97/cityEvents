package com.svalero.cityEvents;

import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.dto.LocationOutDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                  50009, LocalDate.of(2025,2,1), true, null),
            new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                    "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, null),
            new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                    "Independencia", 50002, LocalDate.of(2024,5,3), false, null)
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
    }

    @Test
    public void testFindByCategory() {

        List<Location> mockLocationList = List.of(
                new Location(1,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                        50009, LocalDate.of(2025,2,1), true, null),
                new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, null),
                new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                        "Independencia", 50002, LocalDate.of(2024,5,3), false, null)
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
    }

    @Test
    public void testFindByPostalCode() {

        List<Location> mockLocationList = List.of(
                new Location(1,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                        50009, LocalDate.of(2025,2,1), true, null),
                new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, null),
                new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                        "Independencia", 50001, LocalDate.of(2024,5,3), false, null)
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
    }

    @Test
    public void testFindByDiabledAccessTrue() {

        List<Location> mockLocationList = List.of(
                new Location(1,"La Romareda", "Estadio de fútbol", "Estadio", "Isabel la Católica",
                        50009, LocalDate.of(2025,2,1), true, null),
                new Location(2, "Basília del Pilar", "Edificio cristiano. Elemento más representativo de la ciudad",
                        "Monumento", "Plaza del Pilar", 50001, LocalDate.of(2026,10,2), true, null),
                new Location(3, "Teatro Principal", "Teatro histórico de la ciudad", "Teatro",
                        "Independencia", 50001, LocalDate.of(2024,5,3), false, null)
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
    }


}
