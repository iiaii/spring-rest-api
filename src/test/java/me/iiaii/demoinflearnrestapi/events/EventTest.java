package me.iiaii.demoinflearnrestapi.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    public void builder() throws Exception {
        // given
        String name = "Event";
        String description = "Spring";

        // when
        Event event = Event.builder()
                .name(name)
                .description(description)
                .build();

        // then
        assertThat(event).isNotNull();
    }
    
    @Test
    public void javaBean() throws Exception {
        // given
        String name = "Event";
        String description = "Spring";

        // when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}