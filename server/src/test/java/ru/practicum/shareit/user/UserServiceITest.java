package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceITest {
    private UserServiceImpl userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        UserMapper userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void getUserById() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        UserDto result = userService.getById(userId);
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getAll();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUser() {
        User user = new User(1L, "name", "desc");
        UserDto userDto = new UserDto(1L, "name", "desc");
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.create(userDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void editUser() {
        User user = new User(1L, "name", "desc");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.edit(user.getId(), Collections.emptyMap());
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void editUserName() {
        User user = new User(1L, "name", "desc");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.edit(user.getId(), Map.of("name", "NewName"));
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void editUserEmail() {
        User user = new User(1L, "name", "desc");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.edit(user.getId(), Map.of("email", "newmail@mail.ru"));
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser() {
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
