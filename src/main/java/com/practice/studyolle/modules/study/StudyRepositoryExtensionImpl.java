package com.practice.studyolle.modules.study;

import com.practice.studyolle.modules.account.QAccount;
import com.practice.studyolle.modules.tag.QTag;
import com.practice.studyolle.modules.zone.QZone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

/*
N+1 Select 문제
left(outer) join + fetch Join + distinct 로 해결
1) left join
    - 첫번째 테이블에 연관 관계가 있는 모든 데이터 가져오기. 연관 데이터가 없으면 null로 채워서 가져옴.
    - 첫번째 테이블 컬럼만 본다면 중복 row qkftod
2) fetch Join
    - join 관계의 데이터도 같이 가져온다.
3) distinct
    - 중복 제거

*/
public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension{

    // 자식 생성자를 호출할 때 부모 생성자도 호출됨
    // 없으면 컴파일 에러
    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    /*
    페이징 적용
    1) SQL 의 limit, offset 사용
    2) 스프링 데이터 JPA 가 제공하는 Pageable 사용
     */
    // 페이징 ( 한 페이지 size default 20개)
    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {

        QStudy study = QStudy.study;

        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                        .and(study.title.containsIgnoreCase(keyword))
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .leftJoin(study.managers, QAccount.account).fetchJoin()
                .leftJoin(study.members, QAccount.account).fetchJoin()
                .distinct();

        JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Study> fetchResults = pageableQuery.fetchResults();// 페이징 정보도 리턴

        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }


    /* 여기서 더 최적화를 하려면.
    1. distinct()를 빼는 것 -> 쿼리상 의미없음. join 한 전체 쿼리 자체는 중복되는 것이 없어서 distinct 가 의미없음
    -> 하지만 JPA가 한번 더 해석하면서 fetch 시에 중복을 걸러냄 -> 결과적으론 중복 제거
    -> 하지만 키워드 때문에 의미없이 검사하는 시간 지체됨
    2. result transform 을 제공
    3. 필요한 데이터만 조회하는 projection 활용
     */
/* 노 페이징
    @Override
    public List<Study> findByKeyword(String keyword, Pageable pageable) {

        QStudy study = QStudy.study;

        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                        .and(study.title.containsIgnoreCase(keyword))
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .leftJoin(study.managers, QAccount.account).fetchJoin()
                .leftJoin(study.members, QAccount.account).fetchJoin()
                .distinct();

        return query.fetch();
    }
 */
}
