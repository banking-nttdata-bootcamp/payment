package com.nttdata.bootcamp.events;


import com.nttdata.bootcamp.entity.Payment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentCreatedEventKafka extends EventKafka<Payment> {

}
