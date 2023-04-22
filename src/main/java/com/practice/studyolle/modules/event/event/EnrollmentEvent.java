package com.practice.studyolle.modules.event.event;

import com.practice.studyolle.modules.event.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnrollmentEvent {

    protected final Enrollment enrollment;
    protected final String message;
}

