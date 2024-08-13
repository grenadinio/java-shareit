package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryITest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(1L, "User", "email@mail.ru"));
    }

    @Test
    void findItemRequestByRequesterId() {
        ItemRequest itemRequest = itemRequestRepository.save(
                new ItemRequest(1L, "Description", user, Instant.now()));

        List<ItemRequest> result = itemRequestRepository.findByRequesterId(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(itemRequest.getId(), result.getFirst().getId());
        Assertions.assertEquals(itemRequest.getRequester(), result.getFirst().getRequester());
    }

    @Test
    void findAllByOrderByCreatedDesc() {
        ItemRequest itemRequest = itemRequestRepository.save(
                new ItemRequest(1L, "Description", user, Instant.now()));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));

        Page<ItemRequest> result = itemRequestRepository.findAllByOrderByCreatedDesc(pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(itemRequest.getId(), result.get().findFirst().get().getId());
    }
}
