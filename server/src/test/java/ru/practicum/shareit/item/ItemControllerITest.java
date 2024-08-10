package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerITest {
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getItemById() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.get(any(), any())).thenReturn(new ItemBookingDto());

        mockMvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(itemService, times(1)).get(itemId, userId);
    }

    @Test
    void getAllItemsByUser() throws Exception {
        long userId = 1L;

        when(itemService.getAllByUser(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1)).getAllByUser(userId);
    }

    @Test
    void searchItems() throws Exception {
        long userId = 1L;
        String text = "text";

        when(itemService.search(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1)).search(text);
    }

    @Test
    void createItem() throws Exception {
        long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Name");
        itemDto.setAvailable(true);
        itemDto.setDescription("Description");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String itemJson = objectMapper.writeValueAsString(itemDto);

        when(itemService.create(any(), any())).thenReturn(new ItemDto());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(itemService, times(1)).create(itemDto, userId);
    }

    @Test
    void commentItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        CommentDto comment = new CommentDto();
        comment.setText("Text");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String commentJson = objectMapper.writeValueAsString(comment);

        when(itemService.comment(itemId, userId, comment)).thenReturn(new CommentDto());

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(itemService, times(1)).comment(itemId, userId, comment);
    }

    @Test
    void editItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        Map<String, Object> updates = new HashMap<>();

        when(itemService.edit(any(), any(), any())).thenReturn(new ItemDto());

        mockMvc.perform(patch("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(itemService, times(1)).edit(itemId, userId, updates);
    }
}
