package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Map;

@Transactional(readOnly = true)
public interface UserService {
    UserDto get(Long id);

    Collection<UserDto> getAll();

    @Transactional
    UserDto create(UserDto user);

    @Transactional
    UserDto edit(Long id, Map<String, Object> updates);

    @Transactional
    void delete(Long id);
}
