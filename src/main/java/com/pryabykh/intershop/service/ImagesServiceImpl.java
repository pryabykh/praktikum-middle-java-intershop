package com.pryabykh.intershop.service;

import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.repository.ImageRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImagesServiceImpl implements ImagesService {
    private final ImageRepository imageRepository;

    public ImagesServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Void> download(Long imageId, HttpServletResponse response) {
        return imageRepository.findById(imageId)
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .flatMap(image -> {
                    //todo fixme to reactive style
                    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
                    response.setContentLength(image.getBytes().length);
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + image.getName() + "\"");

                    try (InputStream fileContent = new ByteArrayInputStream(image.getBytes());
                         OutputStream responseOutputStream = response.getOutputStream()) {
                        fileContent.transferTo(responseOutputStream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return Mono.empty();
                });
    }

    @Override
    @Transactional
    public Mono<Long> upload(String base64) {
        Image image = new Image();
        image.setName(UUID.randomUUID() + ".jpg");
        image.setBytes(Base64.getDecoder().decode(base64));
        return imageRepository.save(image).map(Image::getId);
    }
}
