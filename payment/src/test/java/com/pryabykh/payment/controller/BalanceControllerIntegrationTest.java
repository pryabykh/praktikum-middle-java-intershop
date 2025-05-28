package com.pryabykh.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

public class BalanceControllerIntegrationTest extends WebFluxPostgreSQLTestContainerBaseTest {

    @Test
    void fetchBalanceReturnUnauthorizedIfRequestHasNoAuth() {
        webTestClient.get()
                .uri("/balance?user_id=1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    void fetchBalance() throws Exception {
        webTestClient.get()
                .uri("/balance?user_id=1")
                .exchange()
                .expectStatus().isOk();
    }
}
