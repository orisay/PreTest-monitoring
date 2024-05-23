package com.tera.pretest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@EnableRetry
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class PretestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PretestApplication.class, args);
	}

}
