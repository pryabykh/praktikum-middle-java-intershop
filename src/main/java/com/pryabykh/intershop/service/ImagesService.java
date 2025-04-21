package com.pryabykh.intershop.service;

import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

public interface ImagesService {

    Mono<Void> download(Long imageId, HttpServletResponse response);

    Mono<Long> upload(String base64);
}
