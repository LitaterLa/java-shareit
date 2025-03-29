package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(NewItemRequest request, int ownerId) {
        return post("", ownerId, request);
    }

    public ResponseEntity<Object> createComment(NewCommentRequest commentRequest, int itemId, int userId) {
        return post("/" + itemId + "/comment", userId, commentRequest);
    }

    public ResponseEntity<Object> update(int itemId, UpdateItemRequest request, int userId) {
        return patch("/" + itemId, userId, request);
    }

    public ResponseEntity<Object> get(int itemId, int userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUserItems(int ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> delete(int itemId) {
        return delete("/" + itemId);
    }
}
