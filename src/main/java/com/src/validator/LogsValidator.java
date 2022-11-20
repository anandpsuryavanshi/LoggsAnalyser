package com.src.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.src.model.Context;

@Component
public class LogsValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogsValidator.class);

	public void validateInput(Context context, String... args) {
        LOGGER.info("Loggs :: Validating input");
        validateParameters(args);
        validateFilePath(context, args[0]);
    }

    private void validateFilePath(Context context, String logFilePath) {
        LOGGER.info("Loggs :: Log file for Service: ", logFilePath);
        context.setLogFilePath(logFilePath);

        try {
            File file = new ClassPathResource("samples/" + logFilePath).getFile();
            if (!file.exists()) {
                file = new ClassPathResource(logFilePath).getFile();
                if (!file.exists()) {
                    file = new File(logFilePath);
                }
            }

            if (!file.exists())
                throw new FileNotFoundException("Loggs :: Unable to open the file" + logFilePath);
        } catch (IOException e) {
            LOGGER.error("Loggs :: Can't find the specified file '{}'", logFilePath);
        }
    }

    private void validateParameters(String[] args) {
        LOGGER.debug("Loggs :: Validating parameters");
        if (args.length < 1) {
            throw new IllegalArgumentException("Loggs :: Please specify the filepath to analyse.");
        }
    }
}
