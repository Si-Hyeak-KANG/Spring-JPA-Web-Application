package com.practice.studyolle.infra.config;

import com.practice.studyolle.modules.notification.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring boot 에서 Web MVC 설정을 커스터마이징 하고 싶을 때.
 */
// @EnableWebMvc -> Spring boot 에서 제공하는 web MVC 자동설정을 하지 않겠다는 것
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;

    /**
     * notification 핸들러 인터셉터 적용 범위
     * 1) 리다이렉트 요청에는 적용 x
     * 2) static 리소스 요청에는 적용 x
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // excludePathPatterns 에는 문자열과 문자열 타입의 배열만 들어감
        // StaticResourceLocation 이 static 내에 파일 값들을 enum 으로 들고 있음
        // 하지만 각각의 enum 은 List 를 들고 있음
        List<String> staticResourcesPath = Arrays.stream(StaticResourceLocation.values())
                .flatMap(StaticResourceLocation::getPatterns)
                .collect(Collectors.toList());

        staticResourcesPath.add("/node_modules/**");

        registry.addInterceptor(notificationInterceptor)
                .excludePathPatterns(staticResourcesPath);
    }
}
