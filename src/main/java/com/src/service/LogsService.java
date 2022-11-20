package com.src.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.src.manager.LogsManager;
import com.src.model.Context;
import com.src.validator.LogsValidator;

@Service
public class LogsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogsService.class);

	@Autowired
	private LogsValidator validator;

	@Autowired
	private LogsManager manager;

	public void execute(String... args) {
		Context context = Context.getInstance();
		validator.validateInput(context, args);
		manager.parseAndPersistEvents(context);
	}

}

