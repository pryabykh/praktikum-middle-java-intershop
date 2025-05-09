package com.pryabykh.payment.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pryabykh.payment.entity.CartItem;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.List;

public class CartItemListRedisSerializer implements RedisSerializer<List<CartItem>> {

    private final ObjectMapper objectMapper;

    public CartItemListRedisSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(List<CartItem> items) throws SerializationException {
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
    public List<CartItem> deserialize(byte[] bytes) throws SerializationException {
        try {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return objectMapper.readValue(bytes, new TypeReference<List<CartItem>>() {});
        } catch (Exception e) {
            throw new SerializationException("Could not deserialize to Item list", e);
        }
    }
}