package com.practice.studyolle.event;

import com.practice.studyolle.WithAccount;
import com.practice.studyolle.domain.Account;
import com.practice.studyolle.domain.Event;
import com.practice.studyolle.domain.EventType;
import com.practice.studyolle.domain.study.Study;
import com.practice.studyolle.study.StudyControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends StudyControllerTest {

    @Autowired
    EventService eventService;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @WithAccount("test")
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @Test
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account manager = createAccount("test2");
        Study study = createStudy("test-study", manager);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, manager);

        mockMvc.perform(
                        post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account member = accountRepository.findByNickname("test");
        isAccepted(member, event);
    }

    /**
     * 참가 신청은 됐지만, 인원이 꽉차서 test 계정은 대기중 상태
     */
    @WithAccount("test")
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @Test
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account manager = createAccount("test2");
        Study study = createStudy("test-study", manager);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, manager);

        Account member2 = createAccount("test3");
        Account member3 = createAccount("test4");
        eventService.newEnrollment(event, member2);
        eventService.newEnrollment(event, member3);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account member1 = accountRepository.findByNickname("test");
        isNotAccepted(member1, event);
    }

    @WithAccount("test")
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 확정함.")
    @Test
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account manager = createAccount("test2");
        Study study = createStudy("test-study", manager);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, manager);

        Account member1 = accountRepository.findByNickname("test");
        Account member2 = createAccount("test3");
        Account member3 = createAccount("test4");
        eventService.newEnrollment(event, member1);
        eventService.newEnrollment(event, member2);
        eventService.newEnrollment(event, member3);

        isAccepted(member1, event);
        isAccepted(member2, event);
        isNotAccepted(member3, event);

        // member1 모임 참가 취소
        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(member2, event);
        isAccepted(member3, event);

        assertNull(enrollmentRepository.findByEventAndAccount(event, member1));
    }

    @WithAccount("test")
    @DisplayName("참가 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자와 새로운 확정자에 변화 없음")
    @Test
    void not_accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {

        Account manager = createAccount("test2");
        Study study = createStudy("test-study", manager);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, manager);

        Account member1 = accountRepository.findByNickname("test");
        Account member2 = createAccount("test3");
        Account member3 = createAccount("test4");
        eventService.newEnrollment(event, member2);
        eventService.newEnrollment(event, member3);
        eventService.newEnrollment(event, member1);

        isAccepted(member2, event);
        isAccepted(member3, event);
        isNotAccepted(member1, event);

        // 확정이 아닌 member1이 참가를 취소하는 경우
        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(member2, event);
        isAccepted(member3, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, member1));
    }

    @WithAccount("test")
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @Test
    void newEnrollment_to_CONFIRMATIVE_event_not_accepted() throws Exception {
        Account manager = createAccount("test2");
        Study study = createStudy("test-study", manager);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, manager);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll" )
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account member = accountRepository.findByNickname("test");
        assertNotNull(enrollmentRepository.findByEventAndAccount(event, member));
        isNotAccepted(member, event);
    }

    private void isAccepted(Account member, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, member).isAccepted());
    }

    private void isNotAccepted(Account member, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, member).isAccepted());
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account manager) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreateDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(8));
        return eventService.createEvent(event, study, manager);
    }
}
