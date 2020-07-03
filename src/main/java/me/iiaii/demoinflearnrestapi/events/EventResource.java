package me.iiaii.demoinflearnrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Getter
public class EventResource extends RepresentationModel<Event> {

    @JsonUnwrapped
    private Event event;

    public EventResource(Event event, Link... links) {
        this.event = event;
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
