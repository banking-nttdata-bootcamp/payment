package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Payment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface PaymentService {

    public Flux<Payment> findAll();
    public Flux<Payment> findByAccountNumber(String accountNumber);

    public Mono<Payment> findByNumber(String number);
    public Mono<Payment> savePayment(Payment payment);
    public Mono<Payment> updatePayment(Payment payment);
    public Mono<Void> deletePayment(String number);

}
