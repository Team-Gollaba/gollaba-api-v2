package org.tg.gollaba.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PageImplDeserializer extends StdDeserializer<PageImpl<?>> {

    public PageImplDeserializer() {
        super(PageImpl.class);
    }

    @Override
    public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        // content 노드에서 내용 추출
        List<?> content = new ArrayList<>();
        if (node.has("content")) {
            content = ctxt.readValue(
                node.get("content").traverse(p.getCodec()),
                ctxt.getTypeFactory().constructCollectionType(List.class, Object.class)
            );
        }
        
        // 페이징 정보 추출
        int number = node.has("number") ? node.get("number").asInt() : 0;
        int size = node.has("size") ? node.get("size").asInt() : content.size();
        long totalElements = node.has("totalElements") ? node.get("totalElements").asLong() : content.size();
        
        return new PageImpl<>(content, PageRequest.of(number, size), totalElements);
    }
} 