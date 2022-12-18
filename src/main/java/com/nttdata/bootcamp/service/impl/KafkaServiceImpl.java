package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Payment;
import com.nttdata.bootcamp.entity.enums.EventType;
import com.nttdata.bootcamp.events.PaymentCreatedEventKafka;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaTemplate<String, EventKafka<?>> producer;

    @Value("${topic.payment.name}")
    private String topicPayment;

    public void publish(Payment payment) {

        PaymentCreatedEventKafka created = new PaymentCreatedEventKafka();
        created.setData(payment);
        created.setId(UUID.randomUUID().toString());
        created.setType(EventType.CREATED);
        created.setDate(new Date());

        this.producer.send(topicPayment, created);
    }

}
