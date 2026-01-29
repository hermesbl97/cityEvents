package com.svalero.cityEvents;

import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.dto.ReviewOutDto;
import com.svalero.cityEvents.dto.UserOutDto;
import com.svalero.cityEvents.repository.UserRepository;
import com.svalero.cityEvents.service.UserService;
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
public class UserServiceTests {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {

        List<User> mockUserList = List.of(
            new User(1, "martin123", "Jorge", "Ruiz", LocalDate.of(1987,2,5), 615987325, true, null),
            new User(2, "sofiladf", "Sofia", "Labarta", LocalDate.of(1995,1,2), 614784236, false, null),
            new User(3,"mondi", "Mónica", "Díez", LocalDate.of(2003,4,8), 698514736, true, null)
        );

        List<UserOutDto> mockUserOutDto = List.of(
          new UserOutDto(1,"martin123", "Ruiz", LocalDate.of(1987,2,5)),
          new UserOutDto(2, "sofiladf", "Labarta", LocalDate.of(1995,1,2)),
          new UserOutDto(3, "mondi", "Díez", LocalDate.of(2003,4,8))
        );

        when(userRepository.findAll()).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockUserOutDto);

        List<UserOutDto> actualUserList = userService.findAll("",null,null);
        assertEquals(3, actualUserList.size());
        assertEquals("mondi", actualUserList.getLast().getUsername());

        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(0)).findUserByName("");
        verify(userRepository, times(0)).findByBirthDateBefore(null);
        verify(userRepository, times(0)).findByActiveFalse();
    }

    @Test
    public void testFindUserByName() {

        List<User> mockUserList = List.of(
                new User(1, "martin123", "Jorge", "Ruiz", LocalDate.of(1987,2,5), 615987325, true, null),
                new User(2, "sofiladf", "Sofia", "Labarta", LocalDate.of(1995,1,2), 614784236, false, null),
                new User(3,"mondi", "Mónica", "Díez", LocalDate.of(2003,4,8), 698514736, true, null)
        );

        List<UserOutDto> mockUserOutDto = List.of(
                new UserOutDto(3, "mondi", "Díez", LocalDate.of(2003,4,8))
        );

        when(userRepository.findUserByName("Mónica")).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockUserOutDto);

        List<UserOutDto> actualEventList = userService.findAll("Mónica",null,null);
        assertEquals(1, actualEventList.size());
        assertEquals("mondi", actualEventList.getLast().getUsername());

        verify(userRepository, times(0)).findAll();
        verify(userRepository, times(1)).findUserByName("Mónica");
        verify(userRepository, times(0)).findByBirthDateBefore(null);
        verify(userRepository, times(0)).findByActiveFalse();
    }

    @Test
    public void testFindByBirthDateBefore() {

        List<User> mockUserList = List.of(
                new User(1, "martin123", "Jorge", "Ruiz", LocalDate.of(1987,2,5), 615987325, true, null),
                new User(2, "sofiladf", "Sofia", "Labarta", LocalDate.of(1995,1,2), 614784236, false, null),
                new User(3,"mondi", "Mónica", "Díez", LocalDate.of(2003,4,8), 698514736, true, null)
        );

        List<UserOutDto> mockUserOutDto = List.of(
                new UserOutDto(1,"martin123", "Ruiz", LocalDate.of(1987,2,5)),
                new UserOutDto(2, "sofiladf", "Labarta", LocalDate.of(1995,1,2))
        );

        when(userRepository.findByBirthDateBefore(LocalDate.of(2000,1,1))).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockUserOutDto);

        List<UserOutDto> actualUserList = userService.findAll("",LocalDate.of(2000,1,1),null);
        assertEquals(2, actualUserList.size());
        assertEquals("sofiladf", actualUserList.getLast().getUsername());

        verify(userRepository, times(0)).findAll();
        verify(userRepository, times(0)).findUserByName("");
        verify(userRepository, times(1)).findByBirthDateBefore(LocalDate.of(2000,1,1));
        verify(userRepository, times(0)).findByActiveFalse();
    }

    @Test
    public void testFindByActiveFalse() {

        List<User> mockUserList = List.of(
                new User(1, "martin123", "Jorge", "Ruiz", LocalDate.of(1987,2,5), 615987325, true, null),
                new User(2, "sofiladf", "Sofia", "Labarta", LocalDate.of(1995,1,2), 614784236, false, null),
                new User(3,"mondi", "Mónica", "Díez", LocalDate.of(2003,4,8), 698514736, true, null)
        );

        List<UserOutDto> mockUserOutDto = List.of(
                new UserOutDto(2, "sofiladf", "Labarta", LocalDate.of(1995,1,2))
        );

        when(userRepository.findByActiveFalse()).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockUserOutDto);

        List<UserOutDto> actualUserList = userService.findAll("",null,true);
        assertEquals(1, actualUserList.size());
        assertEquals("sofiladf", actualUserList.getLast().getUsername());

        verify(userRepository, times(0)).findAll();
        verify(userRepository, times(0)).findUserByName("");
        verify(userRepository, times(0)).findByBirthDateBefore(null);
        verify(userRepository, times(1)).findByActiveFalse();
    }

}
