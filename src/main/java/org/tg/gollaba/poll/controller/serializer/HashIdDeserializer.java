package org.tg.gollaba.poll.controller.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.web.HashIdHandler;

@Component
@RequiredArgsConstructor
public class HashIdDeserializer extends JsonDeserializer<Long> {
    private final HashIdHandler hashIdHandler;

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) {
        String text;
        try {
            text = p.getText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return hashIdHandler.decode(text);
    }
}
