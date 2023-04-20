package com.practice.studyolle.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

// SimpleAsyncTaskExecutor, 기본 executor 가 있어서 조작없이 사용 가능
// 하지만 매번 새로운 쓰레드를 만들어서 처리하기 때문에
// 쓰레드 생성 과정에서 시간이 더 걸림.
// 풀에 미리 쓰레드를 만들어 두고 사용하는 것이 빠르고 효율적
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // Executor 설정 (풀 사용)
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors(); // 프로세서 개수
        log.info("processor count {}", processors);
        executor.setCorePoolSize(processors); // cpu
        executor.setMaxPoolSize(processors * 2); // cpu
        executor.setQueueCapacity(50); // memory, (기본 Integer.MAX_VALUE[21억])
        executor.setKeepAliveSeconds(60); // 초과(max) Pool size 유지 시간
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        log.warn("Throw Async Exception");
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
    }
}
