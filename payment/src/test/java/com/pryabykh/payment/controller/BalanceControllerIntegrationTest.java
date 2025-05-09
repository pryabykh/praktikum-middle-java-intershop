package com.pryabykh.payment.controller;

import org.junit.jupiter.api.Test;

public class BalanceControllerIntegrationTest extends WebFluxPostgreSQLTestContainerBaseTest {

    @Test
    void fetchBalance() throws Exception {
        webTestClient.get()
                .uri("/balance?user_id=1")
                .exchange()
                .expectStatus().isOk();
    }
}
