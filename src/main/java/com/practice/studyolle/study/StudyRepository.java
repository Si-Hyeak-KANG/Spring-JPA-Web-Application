package com.practice.studyolle.study;

import com.practice.studyolle.domain.study.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByPath(String path);

    // 해당 쿼리를 사용하면서 매핑된 데이터 정보를 조인해서 갖고오면 쿼리 절약
    // db에 여러번 요청할 것이냐 vs db에 무거운 요청을 한번 할 것이냐
    // 추후 요청이 많이 들어올 상황을 생각했을 땐 (예를 들어 사용자가 많아졌을때) 쿼리를 줄이는 방법은 좋은 방법
    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findByPath(String path);
}
