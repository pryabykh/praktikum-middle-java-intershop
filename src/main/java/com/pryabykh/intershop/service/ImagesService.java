package com.pryabykh.intershop.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ImagesService {

    void download(Long imageId, HttpServletResponse response);
}
