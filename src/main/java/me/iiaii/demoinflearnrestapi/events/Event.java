package me.iiaii.demoinflearnrestapi.events;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import me.iiaii.demoinflearnrestapi.accounts.Account;
import me.iiaii.demoinflearnrestapi.accounts.AccountSerializer;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Event extends RepresentationModel<Event> {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임 ​
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;

    public void update() {
        this.free = this.basePrice == 0 && this.maxPrice == 0;
        this.offline = !(this.location == null || this.location.isBlank()); // isBlank 는 trim 할 필요 없이 비어있는지 확인
    }
}
