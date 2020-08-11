package me.iiaii.demoinflearnrestapi.events;

import lombok.RequiredArgsConstructor;
import me.iiaii.demoinflearnrestapi.accounts.Account;
import me.iiaii.demoinflearnrestapi.accounts.AccountAdapter;
import me.iiaii.demoinflearnrestapi.accounts.CurrentUser;
import me.iiaii.demoinflearnrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // 원래는
//        Event event = Event.builder()
//                .name(eventDto.getName())
//                .description()
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(currentUser);
        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler,
                                      @CurrentUser Account account) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        var entityModels = assembler.toModel(page, e -> new EventResource(e));
        entityModels.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));

        if (account != null) {
            entityModels.add(linkTo(EventController.class).withRel("create-event"));
        }

        return ResponseEntity
                .ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account currentUser) {
        Optional<Event> findEvent = this.eventRepository.findById(id);

        if(findEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = findEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-event-get").withRel("profile"));

        if (event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }

        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        Optional<Event> findEvent = this.eventRepository.findById(id);

        if (findEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = findEvent.get();

        if (!event.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        this.modelMapper.map(eventDto, event);
        Event savedEvent = this.eventRepository.save(event);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }


    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
