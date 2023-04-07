package com.practice.studyolle.study.validator;

import com.practice.studyolle.study.GroupRepository;
import com.practice.studyolle.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

    private final GroupRepository groupRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return StudyForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm studyForm = (StudyForm) target;
        if (groupRepository.existsByPath(studyForm.getPath())) {
            errors.rejectValue("path", "wrong.path","해당 스터디 경로값을 사용할 수 없습니다.");
        }
    }
}
