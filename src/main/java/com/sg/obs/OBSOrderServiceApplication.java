package com.sg.obs;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class OBSOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OBSOrderServiceApplication.class, args);
	}

	@PostConstruct
	public static void setTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
	}

}
