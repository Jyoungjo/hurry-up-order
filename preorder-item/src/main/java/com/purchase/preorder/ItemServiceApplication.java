package com.purchase.preorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
		"com.common",
		"com.purchase.preorder"
})
@EnableCaching
@EnableJpaRepositories(basePackages = {
		"com.common.domain.repository.common",
		"com.common.domain.repository.item"
})
@EntityScan(basePackages = {
		"com.common.domain.entity.common",
		"com.common.domain.entity.item"
})
@EnableKafka
@EnableScheduling
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

}
