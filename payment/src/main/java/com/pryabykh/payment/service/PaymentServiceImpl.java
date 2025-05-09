package com.pryabykh.payment.service;

import com.pryabykh.controller.domain.BalanceGet200Response;
import com.pryabykh.controller.domain.PayPost200Response;
import com.pryabykh.payment.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final AccountRepository accountRepository;

    public PaymentServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Mono<PayPost200Response> processPayment(Long userId, Long amount) {

        return accountRepository.findByUserId(userId)
                .flatMap(account -> {
                    if (account.getBalance() < amount) {
                        return Mono.just(new PayPost200Response()
                                .success(false)
                                .message("Insufficient funds"));
                    }
                    account.setBalance(account.getBalance() - amount);
                    return accountRepository.save(account).map(saved ->
                            new PayPost200Response()
                                    .success(true)
                                    .message("Payment successful")
                                    .newBalance(saved.getBalance()));
                })
                .switchIfEmpty(Mono.just(new PayPost200Response()
                        .success(false)
                        .message("User not found")));
    }

    @Override
    @Transactional
    public Mono<BalanceGet200Response> fetchBalance(Long userId) {
        return accountRepository.findByUserId(userId)
                .map(account -> new BalanceGet200Response().userId(userId).balance(account.getBalance()))
                .switchIfEmpty(Mono.empty());
    }
}
