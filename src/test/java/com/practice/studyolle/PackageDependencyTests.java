package com.practice.studyolle;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";

    // 스터디 페키지는 스터디와 이벤트 패키지에서 참조되어진다.
    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage(STUDY)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(STUDY, EVENT);

    // 이벤트 패키지는 이벤트, 스터디, 회원을 참조한다.
    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(EVENT, STUDY, ACCOUNT);

    // 회원 패키지는 회원과 태그와 지역을 참한다.
    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(ACCOUNT, TAG, ZONE);

    // 각각의 모듈 간에 Circular dependency (의존성 순환)가 없는지 체크
    @ArchTest
    ArchRule cycleCheck = slices().matching("com.practice.studyolle.modules.(*)..")
            .should().beFreeOfCycles();

    // 모듈 패키지 내용은 모듈 내에서만 참조
    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.practice.studyolle.modules..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("com.practice.studyolle.modules..");

}
