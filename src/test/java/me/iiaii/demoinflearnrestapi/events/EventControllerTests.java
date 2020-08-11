package me.iiaii.demoinflearnrestapi.events;

import me.iiaii.demoinflearnrestapi.accounts.Account;
import me.iiaii.demoinflearnrestapi.accounts.AccountRepository;
import me.iiaii.demoinflearnrestapi.accounts.AccountRole;
import me.iiaii.demoinflearnrestapi.accounts.AccountService;
import me.iiaii.demoinflearnrestapi.common.AppProperties;
import me.iiaii.demoinflearnrestapi.common.BaseControllerTest;
import me.iiaii.demoinflearnrestapi.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp() {
        this.accountRepository.deleteAll();
        this.eventRepository.deleteAll();
    }

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        // given
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 13, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 13, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 13, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 13, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .build();

        // when
//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        // then
        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("query-events").description("link to query events"),
                            linkWithRel("update-event").description("link to update and existing event"),
                            linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("Date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("Date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("Date time of end of new event"),
                                fieldWithPath("location").description("Location of new event"),
                                fieldWithPath("basePrice").description("Base price of new event"),
                                fieldWithPath("maxPrice").description("Max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("Limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")    // 어떤 타입인지 적어주는 것이 좋을
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("Identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("Date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("Date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("Date time of end of new event"),
                                fieldWithPath("location").description("Location of new event"),
                                fieldWithPath("basePrice").description("Base price of new event"),
                                fieldWithPath("maxPrice").description("Max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("Limit of enrollment"),
                                fieldWithPath("free").description("It tells that this event is free or not"),
                                fieldWithPath("offline").description("It tells that this event is offline or not"),
                                fieldWithPath("eventStatus").description("Event status"),


                                //optional fields
                                fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("Link to self").optional(),
                                fieldWithPath("_links.query-events.href").type(JsonFieldType.STRING).description("Link to query events").optional(),
                                fieldWithPath("_links.update-event.href").type(JsonFieldType.STRING).description("Link to update existing event").optional(),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    private String getBearerToken() throws Exception {
        return "Bearer "+getAccessToken();
    }

    private String getAccessToken() throws Exception {
        // given
        Account user = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(user);


        // when
        // then
        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();

        return parser.parseMap(responseBody).get("access_token").toString();
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        // given
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 13, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 13, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 13, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 13, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        // when
//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        // then
        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        // given
        EventDto eventDto = EventDto.builder().build();


        // when


        // then
        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        // given
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 13, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 13, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 13, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 23, 13, 21))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .build();


        // when


        // then
        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // given
        IntStream.range(0,30).forEach(this::generateEvent);

        // when
        // then
            this.mockMvc.perform(get("/api/events")
                .param("page","1")
                .param("size","10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {
        // given
        IntStream.range(0,30).forEach(this::generateEvent);

        // when
        // then
        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param("page","1")
                .param("size","10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }

    
    @Test
    @TestDescription("기존의 이벤트 하나 조회하기")
    public void getEvent() throws Exception {
        // given
        Event event = this.generateEvent(100);

        // when
        // then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을때 404")
    public void getEvent404() throws Exception {
        // given
        // when
        // then
        this.mockMvc.perform(get("/api/events/1089"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("이벤트 수정 하기")
    public void updateEvent() throws Exception {
        // given
        Event event = this.generateEvent(10);

        String eventName = "Update Event";
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);

        // when
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"))
        ;


        // then

    }


    @Test
    @TestDescription("입력값이 비어있는 경우 - 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception {
        // given
        Event event = this.generateEvent(10);

        EventDto eventDto = new EventDto();

        // when
        // then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력값이 잘못된 경우 - 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception {
        // given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(2000);
        eventDto.setMaxPrice(100);

        // when
        // then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 - 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
        // given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // when
        // then
        this.mockMvc.perform(put("/api/events/123123", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }



    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("Spring"+index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 13, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 13, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 13, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 13, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }

}
