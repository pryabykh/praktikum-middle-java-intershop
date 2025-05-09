package com.pryabykh.payment.service;

import com.pryabykh.controller.domain.BalanceGet200Response;
import com.pryabykh.controller.domain.PayPost200Response;
import com.pryabykh.payment.entity.Account;
import com.pryabykh.payment.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PaymentServiceImpl.class})
public class PaymentServiceTest {
    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(accountRepository);
    }

    @Test
    public void processPaymentPositive() {
        when(accountRepository.findByUserId(anyLong())).thenReturn(Mono.just(new Account(1L, 1L, 999L)));
        when(accountRepository.save(any())).thenReturn(Mono.just(new Account(1L, 1L, 899L)));

        PayPost200Response paymentResult = paymentService.processPayment(1L, 100L).block();

        Assertions.assertEquals("Payment successful", paymentResult.getMessage());
        Assertions.assertEquals(899, paymentResult.getNewBalance());
        Assertions.assertTrue(paymentResult.getSuccess());
    }

    @Test
    public void processPaymentNegative() {
        when(accountRepository.findByUserId(anyLong())).thenReturn(Mono.just(new Account(1L, 1L, 999L)));

        PayPost200Response paymentResult = paymentService.processPayment(1L, 1000L).block();

        Assertions.assertEquals("Insufficient funds", paymentResult.getMessage());
        Assertions.assertFalse(paymentResult.getSuccess());
    }

    @Test
    public void fetchBalance() {
        when(accountRepository.findByUserId(anyLong())).thenReturn(Mono.just(new Account(1L, 1L, 999L)));

        BalanceGet200Response balance = paymentService.fetchBalance(1L).block();

        Assertions.assertEquals(1L, balance.getUserId());
        Assertions.assertEquals(999L, balance.getBalance());
    }
}
