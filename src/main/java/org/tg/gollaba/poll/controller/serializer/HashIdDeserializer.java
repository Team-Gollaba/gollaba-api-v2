package org.tg.gollaba.poll.controller.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.config.ApplicationContextProvider;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class HashIdDeserializer extends JsonDeserializer<Long> {
    private final HashIdHandler hashIdHandler;

    public HashIdDeserializer() {
        this.hashIdHandler = ApplicationContextProvider.getApplicationContext()
            .getBean(HashIdHandler.class);
    }

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return hashIdHandler.decode(p.getValueAsString());
    }
}
