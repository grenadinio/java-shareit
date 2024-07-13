package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> get(Long id);

    Collection<User> getAll();

    User create(User user);

    User edit(User user);

    void delete(Long id);
}
