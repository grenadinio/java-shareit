package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentRepositoryITest {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void findAllCommentsByItemId() {
        User newUser = new User();
        newUser.setEmail("user@mail.ru");
        newUser.setName("User");
        User user = userRepository.save(newUser);

        Item newItem = new Item();
        newItem.setOwner(user);
        newItem.setName("Item");
        newItem.setDescription("Description");
        newItem.setAvailable(true);
        ItemRequest itemRequest = itemRequestRepository.save(
                new ItemRequest(1L, "Description", user, Instant.now()));

        newItem.setRequest(itemRequestRepository.save(itemRequest));
        Item item = itemRepository.save(newItem);

        Comment newComment = new Comment();
        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setText("Text");
        newComment.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(newComment);

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        Assertions.assertNotNull(comments);
        Assertions.assertEquals(1, comments.size());
    }
}
