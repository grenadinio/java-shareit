package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Map;

public interface UserService {
    UserDto get(Long id);

    Collection<UserDto> getAll();

    UserDto create(UserDto user);

    UserDto edit(Long id, Map<String, Object> updates);

    void delete(Long id);
}
