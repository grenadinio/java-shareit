package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getById(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getByUser(Long userId, String state) {
        return get("?state={state}", userId, Map.of("state", state));
    }

    public ResponseEntity<Object> getByOwner(Long userId, String state) {
        return get("/owner?state={state}", userId, Map.of("state", state));
    }

    public ResponseEntity<Object> create(BookingDto booking, Long userId) {
        return post("", userId, booking);
    }

    public ResponseEntity<Object> approve(Long id, boolean approved, Long userId) {
        return patch("/" + id + "?approved={approved}", userId, Map.of("approved", approved), null);
    }
}
