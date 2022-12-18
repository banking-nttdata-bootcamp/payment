package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Payment;
import com.nttdata.bootcamp.repository.PaymentRepository;
import com.nttdata.bootcamp.service.KafkaService;
import com.nttdata.bootcamp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public Flux<Payment> findAll() {
        Flux<Payment> payments = paymentRepository.findAll();
        return payments;
    }

    @Override
    public Flux<Payment> findByAccountNumber(String accountNumber) {
        Flux<Payment> payments = paymentRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return payments;
    }

    @Override
    public Mono<Payment> findByNumber(String Number) {
        Mono<Payment> payment = paymentRepository
                .findAll()
                .filter(x -> x.getPaymentNumber().equals(Number))
                .next();
        return payment;
    }

    @Override
    public Mono<Payment> savePayment(Payment dataPayment) {
        Mono<Payment> paymentMono = findByNumber(dataPayment.getPaymentNumber())
                .flatMap(__ -> Mono.<Payment>error(new Error("This payment number " + dataPayment.getPaymentNumber() + "exists")))
                .switchIfEmpty(saveTopic(dataPayment));
        return paymentMono;


    }

    @Override
    public Mono<Payment> updatePayment(Payment dataPayment) {

        Mono<Payment> transactionMono = findByNumber(dataPayment.getPaymentNumber());
        try {
            dataPayment.setDni(transactionMono.block().getDni());
            dataPayment.setAmount(transactionMono.block().getAmount());
            dataPayment.setCreationDate(transactionMono.block().getCreationDate());
            return paymentRepository.save(dataPayment);
        }catch (Exception e){
            return Mono.<Payment>error(new Error("The payment " + dataPayment.getAccountNumber() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> deletePayment(String Number) {
        Mono<Payment> transactionMono = findByNumber(Number);
        try {
            Payment payment = transactionMono.block();
            return paymentRepository.delete(payment);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("This payment number" + Number+ " do not exists"));
        }
    }
    public Mono<Payment> saveTopic(Payment dataPayment){
        Mono<Payment> monPayment = paymentRepository.save(dataPayment);
        this.kafkaService.publish(monPayment.block());
        return monPayment;
    }






}
