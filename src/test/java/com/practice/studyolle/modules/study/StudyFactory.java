package com.practice.studyolle.modules.study;

import com.practice.studyolle.modules.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyFactory {

    @Autowired StudyService studyService;
    @Autowired StudyRepository studyRepository;

    public Study createStudy(String path, Account manager) {
        Study newStudy = new Study();
        newStudy.setPath(path);
        studyService.createNewStudy(newStudy, manager);
        return newStudy;
    }
}
