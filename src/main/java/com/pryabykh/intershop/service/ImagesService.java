package com.pryabykh.intershop.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ImagesService {

    void upload(Long imageId, HttpServletResponse response);

    Long upload(String base64);
}
