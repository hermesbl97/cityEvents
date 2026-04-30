package com.svalero.cityEvents;

import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.domain.Event;
import com.svalero.cityEvents.domain.Location;
import com.svalero.cityEvents.dto.EventInDto;
import com.svalero.cityEvents.dto.EventModifyInDto;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.exception.EventNotFoundException;
import com.svalero.cityEvents.repository.EventRepository;
import com.svalero.cityEvents.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Event> mockEventList = List.of(
            new Event(1,"Campanadas Año nuevo", "Un año más las campanadas tendrán lugar en la Plaza del pilar",
                    LocalDate.now(), "Evento", 2500, 32, true, null, null, null ),
            new Event(2, "Final Copa Casademont", "Partido femenino en el que el Caseademont Zaragoza puede ganar",
                    LocalDate.now(), "Deporte", 12000, 25, true, null, null, null),
            new Event(3, "Panorama desde el Puente", "Obra de teatro novedosa",
                    LocalDate.of(2025,1,2), "Evento", 300, 25, false, null, null, null)
        );

        List<EventOutDto> mockEventOutDto = List.of(
            new EventOutDto(1,"Campanadas Año nuevo", LocalDate.now(), "Evento", 32),
            new EventOutDto(2, "Final Copa Casademont", LocalDate.now(), "Deporte", 25),
            new EventOutDto(3, "Panorama desde el Puente", LocalDate.of(2025,1,2), "Evento", 25)
        );

        when(eventRepository.findAll()).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType())).thenReturn(mockEventOutDto);

        List<EventOutDto> actualEventList = eventService.findAll("", "", null);
        assertEquals(3, actualEventList.size()); //comprobamos que el tamaño del listado que nos devuelve es de tres
        assertEquals("Evento", actualEventList.getFirst().getCategory());

        verify(eventRepository, times(1)).findAll(); //comprobamos que se haya llamado una vez al método findAll.
        verify(eventRepository, times(0)).findByCategory(""); //comprobamos que no se haya llamado al método por categoría
        verify(eventRepository, times(0)).findByLocation_Name("");
        verify(eventRepository, times(0)).findByPriceLessThanEqualOrderByPriceAsc(30f);
    }

    @Test
    public void testFindAllByCategory() {
        List<Event> mockEventList = List.of(
            new Event(1,"Campanadas Año nuevo", "Un año más las campanadas tendrán lugar en la Plaza del pilar",
                    LocalDate.now(), "Evento", 2500, 32, true, null, null, null ),
            new Event(2, "Final Copa Casademont", "Partido femenino en el que el Caseademont Zaragoza puede ganar",
                    LocalDate.now(), "Deporte", 12000, 25, true, null, null, null),
            new Event(3, "Panorama desde el Puente", "Obra de teatro novedosa",
                    LocalDate.of(2025,1,2), "Evento", 300, 25, false, null, null, null)
        );

        List<EventOutDto> mockModelMapperOut = List.of(
            new EventOutDto(1,"Campanadas Año nuevo", LocalDate.now(), "Evento", 32),
            new EventOutDto(3, "Panorama desde el Puente", LocalDate.of(2025,1,2), "Evento", 18)
        );

        when(eventRepository.findByCategory("Evento")).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType())).thenReturn(mockModelMapperOut);

        List<EventOutDto> actualEventList = eventService.findAll("Evento", "", null);
        assertEquals(2, actualEventList.size());
        assertEquals("Campanadas Año nuevo", actualEventList.getFirst().getName());

        verify(eventRepository, times(0)).findAll();
        verify(eventRepository, times(1)).findByCategory("Evento");
        verify(eventRepository, times(0)).findByLocation_Name("");
        verify(eventRepository, times(0)).findByPriceLessThanEqualOrderByPriceAsc(30f);
    }

    @Test
    public void testFinByLocation_Name() {


        Location location = new Location();
        location.setId(1);
        location.setName("Plaza del Pilar");

        List<Event> mockEventList = List.of(
                new Event(1,"Campanadas Año nuevo", "Un año más las campanadas tendrán lugar en la Plaza del pilar",
                        LocalDate.now(), "Evento", 2500, 32, true, null, null, null ),
                new Event(2, "Final Copa Casademont", "Partido femenino en el que el Caseademont Zaragoza puede ganar",
                        LocalDate.now(), "Deporte", 12000, 25, true, null, null, null),
                new Event(3, "Panorama desde el Puente", "Obra de teatro novedosa",
                        LocalDate.of(2025,1,2), "Evento", 300, 25, false, null, null, null),
                new Event(4, "Ofrenda de flores", "Día ne el que la gente realiza ofrenda a la Virgen del Pilar",
                        LocalDate.of(2025,11,11), "Ceremonia", 15000, 0, true, null, location, null)
        );

        List<EventOutDto> mockModelMapperOut = List.of(
                new EventOutDto(4, "Ofrenda de flores", LocalDate.of(2025,11,11), "Ceremonia", 0)
        );

        when(eventRepository.findByLocation_Name("Plaza del Pilar")).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType())).thenReturn(mockModelMapperOut);

        List<EventOutDto> actualEventList = eventService.findAll(null, "Plaza del Pilar", null);
        assertEquals(1, actualEventList.size());
        assertEquals("Ofrenda de flores", actualEventList.getFirst().getName());

        verify(eventRepository, times(0)).findAll();
        verify(eventRepository, times(1)).findByLocation_Name("Plaza del Pilar");
        verify(eventRepository, times(0)).findByCategory("");
        verify(eventRepository, times(0)).findByPriceLessThanEqualOrderByPriceAsc(30f);
    }

    @Test
    public void testFindByPricelessThanEqualOrderByPriceAsc() {

        List<Event> mockEventList = List.of(
                new Event(1,"Campanadas Año nuevo", "Un año más las campanadas tendrán lugar en la Plaza del pilar",
                        LocalDate.now(), "Evento", 2500, 32, true, null, null, null ),
                new Event(2, "Final Copa Casademont", "Partido femenino en el que el Caseademont Zaragoza puede ganar",
                        LocalDate.now(), "Deporte", 12000, 25, true, null, null, null),
                new Event(3, "Panorama desde el Puente", "Obra de teatro novedosa",
                        LocalDate.of(2025,1,2), "Evento", 300, 25, false, null, null, null)
        );

        List<EventOutDto> mockModelMapperOut = List.of(
                new EventOutDto(2,"Final Copa Casademont", LocalDate.now(), "Deporte", 25),
                new EventOutDto(3,"Panorama desde el Puente", LocalDate.of(2025,1,2), "Evento",25)
        );

        when(eventRepository.findByPriceLessThanEqualOrderByPriceAsc(30f)).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType())).thenReturn(mockModelMapperOut);

        List<EventOutDto> actualEventList = eventService.findAll("", "", 30f);
        assertEquals(2, actualEventList.size());
        assertEquals("Panorama desde el Puente", actualEventList.getLast().getName());

        verify(eventRepository, times(0)).findAll();
        verify(eventRepository, times(1)).findByPriceLessThanEqualOrderByPriceAsc(30f);
        verify(eventRepository, times(0)).findByLocation_Name("");
        verify(eventRepository, times(0)).findByCategory("");
    }


    @Test
    public void testFindById() throws EventNotFoundException {
        Event mockEvent = new Event(3, "Panorama desde el Puente", "Obra de teatro novedosa",
                        LocalDate.of(2025,1,2), "Evento", 300, 25, false, null, null, null);


        when(eventRepository.findById(3L)).thenReturn(Optional.of(mockEvent));

        Event event = eventService.findById(3L);
        assertEquals(300, event.getCapacity());
        assertEquals("Panorama desde el Puente", event.getName());

        verify(eventRepository, times(1)).findById(3L);
    }

    @Test
    public void testFindByIdNotFundArtist() {

        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        //Comprobamos que lanza la excepción
        assertThrows(EventNotFoundException.class, () -> eventService.findById(2L));

        verify(eventRepository, times(1)).findById(2L);
    }


    @Test
    public void testAddEvent() {
        Location location = new Location();
        location.setId(1L);

        EventInDto eventInDto = new EventInDto("Campanadas Año nuevo", "Un año más las campanadas tendrán lugar en la Plaza del pilar",
                LocalDate.of(2025,5,1), "Evento", 2500, 32, 1, null);

        List<Artist> artists = List.of();

        Event registerEvent = new Event(3,"Campanadas Año nuevo", "Un año más las campanadas tendrán lugar en la Plaza del pilar",
                LocalDate.of(2025,5,1), "Evento", 2500, 32,true, null, location, null);

        when(eventRepository.save(any(Event.class))).thenReturn(registerEvent);

        Event result = eventService.add(location, eventInDto, artists);

        assertEquals(registerEvent, result);
        assertEquals("Campanadas Año nuevo", result.getName());

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testModifyEvent() throws EventNotFoundException {

        Location location = new Location();
        List<Artist> artists = new ArrayList<>();

        Event existingEvent = new Event();
        existingEvent.setName("Concierto juvenil");
        existingEvent.setId(1);

        EventModifyInDto updatingEvent = new EventModifyInDto();
        updatingEvent.setName("Concierto rock");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);

        eventService.modify(1L, updatingEvent,location, artists);

        verify(modelMapper).map(updatingEvent,existingEvent);
        verify(eventRepository).save(existingEvent);
    }

    @Test
    public void testModifyEventNotFound() {

        Event event = new Event();
        EventModifyInDto eventModifyInDto = new EventModifyInDto();

        Location location = new Location();
        List<Artist> artists = new ArrayList<>();

        //Simulamos que no existe evento con Id:15
        when(eventRepository.findById(15L)).thenReturn(Optional.empty());

        //Comprobamos que lanza la excepción
//        assertThrows(EventNotFoundException.class, () -> eventService.modify(15L, event));

        try {
            eventService.modify(15L, eventModifyInDto, location, artists);
        } catch (EventNotFoundException enfe) {}

        verify(eventRepository, times(1)).findById(15L);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    public void testDeleteEvent() throws EventNotFoundException {
        Event event = new Event();
        event.setId(15L);

        when(eventRepository.findById(15L)).thenReturn(Optional.of(event));

        eventService.delete(15L);

        verify(eventRepository, times(1)).findById(15L);
        verify(eventRepository, times(1)).delete(event);
    }

    @Test
    public void testDeleteEventNotFound() {

        Event event = new Event();

        when(eventRepository.findById(15L)).thenReturn(Optional.empty());

//        assertThrows(EventNotFoundException.class, () -> eventService.delete(15));
        try {
            eventService.delete(15);
        } catch (EventNotFoundException enfe) {
        }

        verify(eventRepository, times(1)).findById(15L);
        verify(eventRepository, times(0)).delete(any(Event.class));
    }

}
