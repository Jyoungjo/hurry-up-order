package com.purchase.preorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {
		"com.common",
		"com.purchase.preorder"
})
@EnableFeignClients
@EnableJpaRepositories(basePackages = {
		"com.common.domain.repository.common",
		"com.common.domain.repository.payment"
})
@EntityScan(basePackages = {
		"com.common.domain.entity.common",
		"com.common.domain.entity.payment"
})
@EnableKafka
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
