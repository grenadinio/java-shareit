package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerITest {
    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllSelfItemRequests() throws Exception {
        long userId = 1L;
        when(itemRequestService.getAllSelf(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).getAllSelf(userId);
    }

    @Test
    void getItemRequestById() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        when(itemRequestService.getById(anyLong())).thenReturn(new ItemRequestDto());

        mockMvc.perform(get("/requests/" + requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(itemRequestService, times(1)).getById(requestId);
    }

    @Test
    void getAllOthersItemRequests() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;
        when(itemRequestService.getAllOthers(anyLong(), anyInt(), anyInt())).thenReturn(Page.empty());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());
        verify(itemRequestService, times(1)).getAllOthers(userId, from, size);
    }

    @Test
    void createItemRequest() throws Exception {
        long userId = 1L;

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setRequesterId(userId);
        itemRequestDto.setDescription("Description");
        itemRequestDto.setItems(Collections.emptyList());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String itemRequestJson = objectMapper.writeValueAsString(itemRequestDto);

        when(itemRequestService.create(anyLong(), any())).thenReturn(new ItemRequestDto());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(itemRequestService, times(1)).create(eq(userId), eq(itemRequestDto));
    }
}
