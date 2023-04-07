package com.practice.studyolle.study;

import com.practice.studyolle.domain.Account;
import com.practice.studyolle.domain.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository repository;

    public Group createNewStudy(Group group, Account account) {
        Group newGroup = repository.save(group);
        newGroup.addManager(account);
        return newGroup;
    }
}
