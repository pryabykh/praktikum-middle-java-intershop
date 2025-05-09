package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.service.ImagesService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class ImagesController {
    private final ImagesService imagesService;

    public ImagesController(ImagesService imagesService) {
        this.imagesService = imagesService;
    }

    @GetMapping("/images/{imageId}")
    public Mono<Void> downloadImage(@PathVariable("imageId") Long imageId,
                                    ServerHttpResponse response) {
        return imagesService.download(imageId, response);
    }
}
