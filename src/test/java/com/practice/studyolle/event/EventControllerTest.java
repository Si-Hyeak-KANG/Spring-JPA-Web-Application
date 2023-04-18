package com.practice.studyolle.event;

import com.practice.studyolle.WithAccount;
import com.practice.studyolle.domain.Account;
import com.practice.studyolle.study.StudyControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EventControllerTest extends StudyControllerTest {

    @Autowired EventService eventService;
    @Autowired EnrollmentRepository enrollmentRepository;

    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @Test
    @WithAccount("test")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {

    }
}
