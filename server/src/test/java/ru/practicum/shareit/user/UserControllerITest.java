package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerITest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUserById() throws Exception {
        long userId = 1L;
        when(userService.getById(userId)).thenReturn(new UserDto());

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(userService, times(1)).getById(userId);
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(userService, times(1)).getAll();
    }

    @Test
    void createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("email@mail.ru");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(userDto);

        when(userService.create(any())).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().json("{}"));
        verify(userService, times(1)).create(userDto);
    }

    @Test
    void editUser() throws Exception {
        long userId = 1L;
        Map<String, Object> updates = new HashMap<>();

        when(userService.edit(any(), any())).thenReturn(new UserDto());

        mockMvc.perform(patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(userService, times(1)).edit(userId, updates);
    }

    @Test
    void deleteUser() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/users/" + userId)
                        .content("{}"))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(userId);
    }
}
