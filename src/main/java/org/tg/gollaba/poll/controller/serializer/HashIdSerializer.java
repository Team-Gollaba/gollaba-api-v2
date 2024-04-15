package org.tg.gollaba.poll.controller.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.tg.gollaba.common.web.HashIdHandler;
import org.tg.gollaba.config.ApplicationContextProvider;

import java.io.IOException;

public class HashIdSerializer extends JsonSerializer<Long> {
    private final HashIdHandler hashIdHandler;

    public HashIdSerializer() {
        this.hashIdHandler = ApplicationContextProvider.getApplicationContext()
            .getBean(HashIdHandler.class);
    }

    @Override
    public void serialize(Long value,
                          JsonGenerator gen,
                          SerializerProvider serializers) {
        try {
            gen.writeString(hashIdHandler.encode(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
