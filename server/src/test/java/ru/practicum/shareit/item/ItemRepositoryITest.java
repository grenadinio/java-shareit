package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryITest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User user;
    private User requester;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(1L, "Name", "email@mail.ru"));
        requester = userRepository.save(new User(2L, "Requester", "requester@mail.ru"));
        itemRequest = itemRequestRepository.save(
                new ItemRequest(1L, "Description", requester, Instant.now()));
    }

    private Item createItem(String name, String description, boolean available, User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        return itemRepository.save(item);
    }

    @Test
    void searchItemByTextInDescription() {
        String text = "Text";
        Item item = createItem("Name", "DescriptionText", true, user, itemRequest);

        List<Item> result = itemRepository.searchAllByTextInNameOrDescription(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(item.getId(), result.getFirst().getId());
        Assertions.assertTrue(result.getFirst().getDescription().toLowerCase().contains(text.toLowerCase()));
    }

    @Test
    void searchItemByTextInName() {
        String text = "Text";
        Item item = createItem("NameText", "Description", true, user, itemRequest);

        List<Item> result = itemRepository.searchAllByTextInNameOrDescription(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(item.getId(), result.getFirst().getId());
        Assertions.assertTrue(result.getFirst().getName().toLowerCase().contains(text.toLowerCase()));
    }

    @Test
    void searchItemByTextInDescriptionUpperCase() {
        String text = "Text";
        Item item = createItem("Name", "DescrTEXTiption", true, user, itemRequest);

        List<Item> result = itemRepository.searchAllByTextInNameOrDescription(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(item.getId(), result.getFirst().getId());
        Assertions.assertTrue(result.getFirst().getDescription().toLowerCase().contains(text.toLowerCase()));
    }

    @Test
    void findAllByOwnerId() {
        Item item = createItem("Name", "Description", true, user, itemRequest);

        List<Item> result = itemRepository.findAllByOwnerId(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(item.getId(), result.getFirst().getId());
        Assertions.assertEquals(item.getOwner(), result.getFirst().getOwner());
    }

    @Test
    void findAllByRequestId() {
        Item item = createItem("Name", "Description", true, user, itemRequest);

        List<Item> result = itemRepository.findAllByOwnerId(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(item.getId(), result.getFirst().getId());
        Assertions.assertEquals(item.getRequest(), result.getFirst().getRequest());
    }
}
