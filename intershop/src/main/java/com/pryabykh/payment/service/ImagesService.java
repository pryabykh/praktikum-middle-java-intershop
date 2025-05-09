package com.pryabykh.payment.service;

import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

public interface ImagesService {

    Mono<Void> download(Long imageId, ServerHttpResponse response);

    Mono<Long> upload(String base64);
}
