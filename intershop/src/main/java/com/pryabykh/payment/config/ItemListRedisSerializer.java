package com.pryabykh.payment.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pryabykh.payment.entity.Item;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.List;

public class ItemListRedisSerializer implements RedisSerializer<List<Item>> {

    private final ObjectMapper objectMapper;

    public ItemListRedisSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(List<Item> items) throws SerializationException {
        try {
            if (items == null) {
                return new byte[0];
            }
            return objectMapper.writeValueAsBytes(items);
        } catch (Exception e) {
            throw new SerializationException("Could not serialize Item list", e);
        }
    }

    @Override
    public List<Item> deserialize(byte[] bytes) throws SerializationException {
        try {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return objectMapper.readValue(bytes, new TypeReference<List<Item>>() {});
        } catch (Exception e) {
            throw new SerializationException("Could not deserialize to Item list", e);
        }
    }
}