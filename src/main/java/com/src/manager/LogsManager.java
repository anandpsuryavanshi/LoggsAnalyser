package com.src.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.src.dao.Alert;
import com.src.dao.AlertRepository;
import com.src.model.ApplicationData;
import com.src.model.Context;
import com.src.model.Event;
import com.src.model.State;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class LogsManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogsManager.class);

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ApplicationData applicationData;

    public void parseAndPersistEvents(Context context) {
        
        Map<String, Event> eventMap = new HashMap<>();
        Map<String, Alert> alerts = new HashMap<>();

        LOGGER.info("Logss :: Parsing the events and persisting the alerts.");
        try (LineIterator li = FileUtils.lineIterator(new ClassPathResource("samples/" + context.getLogFilePath()).getFile())) {
            String line = null;
            while (li.hasNext()) {
                Event event;
                try {
                    event = new ObjectMapper().readValue(li.nextLine(), Event.class);                   
                    if (eventMap.containsKey(event.getId())) {
                        Event e1 = eventMap.get(event.getId());
                        long executionTime = getEventExecutionTime(event, e1);
                        Alert alert = new Alert(event, Math.toIntExact(executionTime));
                        if (executionTime > applicationData.getAlertThresholdMs()) {
                            alert.setAlert(Boolean.TRUE);
                            LOGGER.trace("Loggs :: Execution time for the event {} is {}ms", event.getId(), executionTime);
                        }

                        alerts.put(event.getId(), alert);
                        eventMap.remove(event.getId());
                    } else {
                        eventMap.put(event.getId(), event);
                    }
                } catch (JsonProcessingException e) {
                    LOGGER.error("Loggs :: Unable to parse the event!", e.getMessage());
                }

                if (alerts.size() > applicationData.getTableRowsWriteoffCount()) {
                    persistAlerts(alerts.values());
                    alerts = new HashMap<>();
                }
            } // END while
            if (alerts.size() != 0) {
                persistAlerts(alerts.values());
            }
        } catch (IOException e) {
            LOGGER.error("Loggs :: Unable to access the file:", e.getMessage());
        }
    }

    private void persistAlerts(Collection<Alert> alerts) {
        LOGGER.debug("Loggs :: Persisting alerts :", alerts.size());
        alertRepository.saveAll(alerts);
    }

    private long getEventExecutionTime(Event event1, Event event2) {
        Event endEvent = Stream.of(event1, event2).filter(e -> State.FINISHED.equals(e.getState())).findFirst().orElse(null);
        Event startEvent = Stream.of(event1, event2).filter(e -> State.STARTED.equals(e.getState())).findFirst().orElse(null);

        return Objects.requireNonNull(endEvent).getTimestamp() - Objects.requireNonNull(startEvent).getTimestamp();
    }
}
