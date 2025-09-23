package ru.practicum.ewm.controller.publiccontrol;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.event.EventService;

@RestController
@Slf4j
@RequestMapping ("/public/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventService eventService;


}
