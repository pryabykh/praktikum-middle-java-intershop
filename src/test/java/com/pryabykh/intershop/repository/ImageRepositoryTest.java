package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageRepositoryTest extends DataJpaPostgreSQLTestContainerBaseTest {

    @Autowired
    private ImageRepository imageRepository;

    @Test
    @Sql(statements = {
            "insert into intershop.images (name, bytes) values ('small_image.png', decode('74657374', 'hex'));"
    })
    void findById_whenEntityExists_shouldReturnIt() {
        Optional<Image> image = imageRepository.findById(
                imageRepository.findAll().stream().findFirst().map(Image::getId).orElseThrow()
        );
        assertNotNull(image);
        assertTrue(image.isPresent());
        assertNotNull(image.get().getId());
        assertEquals("small_image.png", image.get().getName());
        assertNotNull(image.get().getBytes());
    }
}
