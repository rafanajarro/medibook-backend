package com.medibook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MedibookApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedibookApplication.class, args);
	}

}
