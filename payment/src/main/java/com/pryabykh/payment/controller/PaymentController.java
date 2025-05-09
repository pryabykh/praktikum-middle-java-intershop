package com.pryabykh.payment.controller;

import com.pryabykh.controller.domain.PayPost200Response;
import com.pryabykh.controller.domain.PayPostRequest;
import com.pryabykh.controller.payment.api.PaymentsApi;
import com.pryabykh.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class PaymentController implements PaymentsApi {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Mono<ResponseEntity<PayPost200Response>> payPost(Mono<PayPostRequest> payPostRequest, ServerWebExchange exchange) {
        return payPostRequest.flatMap(request -> {
            return paymentService.processPayment(request.getUserId(), request.getAmount())
                    .map(result -> result.getSuccess()
                            ? ResponseEntity.ok(result)
                            : ResponseEntity.badRequest().body(result));
        });
    }
}
