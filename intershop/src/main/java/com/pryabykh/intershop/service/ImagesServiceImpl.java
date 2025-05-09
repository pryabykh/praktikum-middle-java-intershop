package com.pryabykh.intershop.service;

import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.repository.ImageRepository;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
    public Mono<Void> download(Long imageId, ServerHttpResponse response) {
        return imageRepository.findById(imageId)
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .flatMap(image -> {
                    response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    response.getHeaders().setContentDispositionFormData("attachment", image.getName());
                    response.getHeaders().setContentLength(image.getBytes().length);

                    DataBuffer dataBuffer = response.bufferFactory().wrap(image.getBytes());

                    return response.writeWith(Mono.just(dataBuffer))
                            .doOnError(error -> DataBufferUtils.release(dataBuffer));
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
