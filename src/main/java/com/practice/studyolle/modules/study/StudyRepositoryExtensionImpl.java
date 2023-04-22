package com.practice.studyolle.modules.study;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension{

    // 자식 생성자를 호출할 때 부모 생성자도 호출됨
    // 없으면 컴파일 에러
    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public List<Study> findByKeyword(String keyword) {

        QStudy study = QStudy.study;

        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                .and(study.title.containsIgnoreCase(keyword))
                .or(study.tags.any().title.containsIgnoreCase(keyword))
                .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)));

        return query.fetch();
    }
}
