package com.practice.studyolle.event;

import com.practice.studyolle.domain.Account;
import com.practice.studyolle.domain.Event;
import com.practice.studyolle.domain.study.Study;
import com.practice.studyolle.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreateDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        // TODO 선착순 모임의 경우, 모집 인원을 늘리면 대기 중인 인원이 자동으로 추가 인원에 맞게 참가 확정 상태로 변경
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}
