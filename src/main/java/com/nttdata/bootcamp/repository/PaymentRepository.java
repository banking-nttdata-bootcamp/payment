package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Payment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//Mongodb Repository
public interface PaymentRepository extends ReactiveCrudRepository<Payment, String> {
}
