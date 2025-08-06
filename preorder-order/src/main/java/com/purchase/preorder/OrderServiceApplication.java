package com.purchase.preorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
		"com.common",
		"com.purchase.preorder"
})
@EnableFeignClients
@EnableScheduling
@EnableJpaRepositories(basePackages = {
		"com.common.domain.repository.common",
		"com.common.domain.repository.order"
})
@EntityScan(basePackages = {
		"com.common.domain.entity.common",
		"com.common.domain.entity.order"
})
@EnableKafka
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
