package com.lhmtech.messaging.rabbit

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ComponentScan(basePackages = ["com.lhmtech.messaging.rabbit"] )
class Application {

	//this is for test
	static void main(String[] args) {
		SpringApplication.run Application, args
	}
}
