package ru.practicum.events.convertors;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.practicum.events.enums.EventState;
import ru.practicum.ewm.handler.exception.ForbiddenOperationException;

import static ru.practicum.util.Constants.VALUE_CANCEL_REVIEW;
import static ru.practicum.util.Constants.VALUE_SEND_TO_REVIEW;

@Component
public class StringToEventStateConverter extends StdConverter<String, EventState> {
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(StringToEventStateConverter.class);

    @Override
    public EventState convert(String value) {
        if (value.equals(VALUE_SEND_TO_REVIEW)) {
            return EventState.PENDING;
        } else if (value.equals(VALUE_CANCEL_REVIEW)) {
            return EventState.CANCELED;
        } else {
            throw new ForbiddenOperationException("Only pending or canceled events can be changed.", log);
        }
    }
}
