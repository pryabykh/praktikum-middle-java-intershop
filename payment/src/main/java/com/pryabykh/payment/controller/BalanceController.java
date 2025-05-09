package com.pryabykh.payment.controller;

import com.pryabykh.controller.domain.BalanceGet200Response;
import com.pryabykh.controller.payment.api.BalanceApi;
import com.pryabykh.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class BalanceController implements BalanceApi {
    private final PaymentService paymentService;

    public BalanceController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Mono<ResponseEntity<BalanceGet200Response>> balanceGet(Long userId, ServerWebExchange exchange) {
        return paymentService.fetchBalance(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
