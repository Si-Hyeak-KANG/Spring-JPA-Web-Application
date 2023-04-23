package com.practice.studyolle.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {

    boolean existsByPath(String path);

    Study findStudyOnlyByPath(String path);

    // 해당 쿼리를 사용하면서 매핑된 데이터 정보를 조인해서 갖고오면 쿼리 절약
    // db에 여러번 요청할 것이냐 vs db에 무거운 요청을 한번 할 것이냐
    // 추후 요청이 많이 들어올 상황을 생각했을 땐 (예를 들어 사용자가 많아졌을때) 쿼리를 줄이는 방법은 좋은 방법
    @EntityGraph(attributePaths = {"managers", "members", "tags", "zones"},
            type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);
    // with tags 는 JPA 가 키워드로 인식하지 않음

    @EntityGraph(attributePaths = {"tags", "managers"})
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(attributePaths = "managers")
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Study findStudyWithMembersByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones"})
    Study findStudyWithTagsAndZonesById(Long id);

    // @EntityGraph 정의 방식
    // 1) 이름을 정의
    // @EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    // 2) 기본 타입이 Fetch 이기 때문에 줄일 수 있음
    // @EntityGraph(attributePaths = {"members", "managers"})
    @EntityGraph(attributePaths = {"members", "managers"})
    Study findStudyWithManagersAndMembersById(Long id);

    List<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);
}
