package com.svalero.cityEvents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.cityEvents.controller.UserController;
import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.domain.User;
import com.svalero.cityEvents.dto.ArtistOutDto;
import com.svalero.cityEvents.dto.UserInDto;
import com.svalero.cityEvents.dto.UserOutDto;
import com.svalero.cityEvents.exception.ArtistNotFoundException;
import com.svalero.cityEvents.exception.UserNotFoundException;
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

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private UserService userService;

    @Test
    public void testGetAll() throws Exception {

        List<UserOutDto> userOutDto = List.of(
                new UserOutDto(1,"martin123", "Ruiz", LocalDate.of(1987,2,5)),
                new UserOutDto(2, "sofiladf", "Labarta", LocalDate.of(1995,1,2)),
                new UserOutDto(3, "mondi", "Díez", LocalDate.of(2003,4,8))
        );

        when(userService.findAll(null,null,null)).thenReturn(userOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/usuarios")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(3, usersListResponse.size());
        assertEquals("mondi", usersListResponse.getLast().getUsername());
    }

    @Test
    public void testGetAllByName() throws Exception {
        List<UserOutDto> usersOutDto = List.of(
                new UserOutDto(3, "mondi", "Díez", LocalDate.of(2003,4,8))
        );

        when(userService.findAll("Mónica",null,null)).thenReturn(usersOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/usuarios")
                        .queryParam("name", "Mónica")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, usersListResponse.size());
        assertEquals("mondi", usersListResponse.getLast().getUsername());
    }

    @Test
    public void testGetAllByBirthDateBefore() throws Exception {
        List<UserOutDto> usersOutDto = List.of(
                new UserOutDto(1,"martin123", "Ruiz", LocalDate.of(1987,2,5)),
                new UserOutDto(2, "sofiladf", "Labarta", LocalDate.of(1995,1,2))
        );

        when(userService.findAll(null,LocalDate.of(2000,1,1),null)).thenReturn(usersOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/usuarios")
                        .queryParam("date", "2000-01-01")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, usersListResponse.size());
        assertEquals("sofiladf", usersListResponse.getLast().getUsername());
    }

    @Test
    public void testGetAllByActiveFalse() throws Exception {
        List<UserOutDto> usersOutDto = List.of(
                new UserOutDto(1,"martin123", "Ruiz", LocalDate.of(1987,2,5)),
                new UserOutDto(2, "sofiladf", "Labarta", LocalDate.of(1995,1,2))
        );

        when(userService.findAll(null,null,true)).thenReturn(usersOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/usuarios")
                        .queryParam("active", "true")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, usersListResponse.size());
        assertEquals("martin123", usersListResponse.getFirst().getUsername());
    }

    @Test
    public void testGetUserById() throws Exception {

        User user = new User(1, "martin123", "Jorge", "Ruiz", LocalDate.of(1987,2,5), 615987325, true, null);

        when(userService.findUserById(2L)).thenReturn(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/usuarios/2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Artist userResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("Jorge", userResponse.getName());
        verify(userService, times(1)).findUserById(2L);
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        when(userService.findUserById(47L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/usuarios/47")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(userService, times(1)).findUserById(47L);
    }

    @Test
    public void testAddUser() throws Exception {

        UserInDto userInDto = new UserInDto("martin123", "Jorge", "Ruiz", LocalDate.of(1987,2,5), 615987325);

        User newUser = new User();

        when(userService.add(userInDto)).thenReturn(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInDto)))
                        .andExpect(status().isCreated())
                        .andReturn();

        verify(userService, times(1)).add(userInDto);
    }

    @Test
    public void testAddUserValidationError400() throws Exception {
        User notValidUser = new User();

        mockMvc.perform(MockMvcRequestBuilders.post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidUser)))
                        .andExpect(status().isBadRequest())
                        .andReturn();

        verifyNoInteractions(userService);
    }

    @Test
    public void testModifyUser() throws Exception {
        User userRequest = new User();
        userRequest.setName("María");

        User userResponse = new User();
        userResponse.setId(35L);
        userResponse.setName("Nuria");

        when(userService.modify(eq(35L), any(User.class))).thenReturn(userResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/usuarios/35")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userRequest)))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        User response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("Nuria", response.getName());
        verify(userService, times(1)).modify(eq(35L),any(User.class));
    }

    @Test
    public void testModifyUserNotFound() throws Exception {

        User userRequest = new User();
        userRequest.setName("María");

        when(userService.modify(eq(35L), any(User.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/usuarios/35")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userRequest)))
                        .andExpect(status().isNotFound());

        verify(userService, times(1)).modify(eq(35L), any(User.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(37L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/usuarios/37"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(37L);
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {

        doThrow(new UserNotFoundException()).when(userService).delete(2L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/usuarios/2"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).delete(2L);
    }
}
