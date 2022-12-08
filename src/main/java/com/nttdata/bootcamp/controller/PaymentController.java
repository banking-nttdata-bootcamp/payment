package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Payment;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/payment")
public class PaymentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private PaymentService paymentService;

	//Deposits search
	@GetMapping("/findAllPayments")
	public Flux<Payment> findAllPayments() {
		Flux<Payment> payments = paymentService.findAll();
		LOGGER.info("Registered payments: " + payments);
		return payments;
	}

	//Deposits by AccountNumber
	@GetMapping("/findAllPaymentsByAccountNumber/{accountNumber}")
	public Flux<Payment> findAllPaymentsByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<Payment> payments = paymentService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered payments of account number: "+accountNumber +"-" + payments);
		return payments;
	}

	//Deposits  by Number
	@CircuitBreaker(name = "payments", fallbackMethod = "fallBackGetPayments")
	@GetMapping("/findByPaymentsNumber/{numberPayment}")
	public Mono<Payment> findByPaymentNumber(@PathVariable("numberPayment") String numberPayment) {
		LOGGER.info("Searching payments by number: " + numberPayment);
		return paymentService.findByNumber(numberPayment);
	}

	//Save deposit
	@CircuitBreaker(name = "payments", fallbackMethod = "fallBackGetPayments")
	@PostMapping(value = "/savePayment")
	public Mono<Payment> savePayment(@RequestBody Payment dataPayment){
		Mono.just(dataPayment).doOnNext(t -> {
					t.setTypeAccount("active");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPayment).onErrorResume(e -> Mono.just(dataPayment))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Payment> depositMono = paymentService.savePayment(dataPayment);
		return depositMono;
	}

	//Update deposit
	@CircuitBreaker(name = "payments", fallbackMethod = "fallBackGetPayments")
	@PutMapping("/updatePayment/{numberTransaction}")
	public Mono<Payment> updatePayment(@PathVariable("numberTransaction") String numberTransaction,
									   @Valid @RequestBody Payment dataPayment) {
		Mono.just(dataPayment).doOnNext(t -> {

					t.setPaymentNumber(numberTransaction);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPayment).onErrorResume(e -> Mono.just(dataPayment))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Payment> updateDeposit = paymentService.updatePayment(dataPayment);
		return updateDeposit;
	}


	//Delete deposit
	@CircuitBreaker(name = "payments", fallbackMethod = "fallBackGetPayments")
	@DeleteMapping("/deletePayment/{numberTransaction}")
	public Mono<Void> deletePayment(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Deleting payments by number: " + numberTransaction);
		Mono<Void> delete = paymentService.deletePayment(numberTransaction);
		return delete;

	}


	@GetMapping("/getCountPayments{accountNumber}")
	//get count of transaction
	public Mono<Long> getCountPayments(@PathVariable("accountNumber") String accountNumber){
		Flux<Payment> payments= findAllPaymentsByAccountNumber(accountNumber);
		return payments.count();
	}


	private Mono<Payment> fallBackGetPayments(Exception e){
		Payment payment= new Payment();
		Mono<Payment> staffMono= Mono.just(payment);
		return staffMono;
	}


}
