package com.pryabykh.payment.service;

import com.pryabykh.controller.domain.BalanceGet200Response;
import com.pryabykh.controller.domain.PayPost200Response;
import reactor.core.publisher.Mono;

public interface PaymentService {

    Mono<PayPost200Response> processPayment(Long userId, Long amount);

    Mono<BalanceGet200Response> fetchBalance(Long userId);
}
