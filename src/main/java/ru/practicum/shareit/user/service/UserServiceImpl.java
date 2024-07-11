package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto get(Long userId) {
        return userMapper.toUserDto(userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден.")));
    }

    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto create(UserDto user) {
        validateEmailUniqueness(user.getId(), user.getEmail());
        return userMapper.toUserDto(userRepository.create(userMapper.toUser(user)));
    }

    public UserDto edit(Long id, Map<String, Object> updates) {
        User user = userRepository.get(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName((String) value);
                    break;
                case "email":
                    validateEmailUniqueness(id, (String) value);
                    user.setEmail((String) value);
                    break;
            }
        });
        return userMapper.toUserDto(userRepository.edit(user));
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    private void validateEmailUniqueness(Long id, String email) {
        for (User existedUser : userRepository.getAll()) {
            if (email.equals(existedUser.getEmail()) && !id.equals(existedUser.getId())) {
                throw new DuplicateDataException("Этот email уже используется.");
            }
        }
    }
}

