package com.pryabykh.intershop.client;

import com.pryabykh.intershop.client.api.BalanceApi;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BalanceApiClient extends BalanceApi {

    @Value("${payment.url:#{null}}")
    private String basePath;

    @PostConstruct
    public void init() {
        if (basePath != null) {
            this.getApiClient().setBasePath(this.basePath);
        }
    }
}
