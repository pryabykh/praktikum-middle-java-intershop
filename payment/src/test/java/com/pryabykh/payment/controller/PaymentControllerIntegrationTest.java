package com.pryabykh.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

public class PaymentControllerIntegrationTest extends WebFluxPostgreSQLTestContainerBaseTest {

    @Test
    void payReturnUnauthorizedIfRequestHasNoAuth() {
        webTestClient.post()
                .uri("/pay")
                .bodyValue("{\"amount\": 100, \"user_id\": 1}")
                .header("content-type", "application/json")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    void pay() throws Exception {
        webTestClient.post()
                .uri("/pay")
                .bodyValue("{\"amount\": 100, \"user_id\": 1}")
                .header("content-type", "application/json")
                .exchange()
                .expectStatus().isOk();
    }
}
