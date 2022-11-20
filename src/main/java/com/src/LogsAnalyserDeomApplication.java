package com.src;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.src.service.LogsService;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class LogsAnalyserDeomApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogsAnalyserDeomApplication.class);

	@Autowired
	private LogsService service;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(LogsAnalyserDeomApplication.class);
		app.run(args);		
	}
	
	 public void run(String... args) {
	        Instant start = Instant.now();
	        service.execute(args);
	        Instant end = Instant.now();
	        LOGGER.info("Total time: {}ms", Duration.between(start, end).toMillis());
	 }
}
