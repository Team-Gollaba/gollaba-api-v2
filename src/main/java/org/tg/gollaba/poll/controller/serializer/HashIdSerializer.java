package org.tg.gollaba.poll.controller.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tg.gollaba.poll.component.HashIdHandler;

import java.io.IOException;

@Component
@NoArgsConstructor
public class HashIdSerializer extends JsonSerializer<Long> {
    @Autowired(required = true)
    private HashIdHandler hashIdHandler;

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
