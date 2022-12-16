package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Payment;

public interface KafkaService {
    void publish(Payment payment);
}
