package com.pryabykh.payment.service;

import com.pryabykh.payment.entity.Image;
import com.pryabykh.payment.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ImagesServiceImpl.class})
public class ImagesServiceTest {
    @Autowired
    private ImagesService imagesService;

    @MockitoBean
    private ImageRepository imageRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(imageRepository);
    }

    @Test
    void findAll_whenNameIsNotProvided_shouldNotApplyFilters() {
        Image image = new Image();
        image.setId(1L);
        image.setName("test.jpg");
        image.setBytes("test.jpg".getBytes(StandardCharsets.UTF_8));

        when(imageRepository.findById(eq(1L))).thenReturn(Mono.just(image));

        imagesService.download(1L, new ServerHttpResponse () {

            @Override
            public HttpHeaders getHeaders() {
                return null;
            }

            @Override
            public DataBufferFactory bufferFactory() {
                return null;
            }

            @Override
            public void beforeCommit(Supplier<? extends Mono<Void>> action) {

            }

            @Override
            public boolean isCommitted() {
                return false;
            }

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return Mono.empty().then();
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return null;
            }

            @Override
            public Mono<Void> setComplete() {
                return null;
            }

            @Override
            public boolean setStatusCode(HttpStatusCode status) {
                return false;
            }

            @Override
            public HttpStatusCode getStatusCode() {
                return null;
            }

            @Override
            public MultiValueMap<String, ResponseCookie> getCookies() {
                return null;
            }

            @Override
            public void addCookie(ResponseCookie cookie) {

            }
        });
    }
}
