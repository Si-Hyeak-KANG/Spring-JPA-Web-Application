package com.practice.studyolle.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up", "/check-email-token",
                        "/email-login","/login-by-email", "/search/study").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .and()
                .logout().logoutSuccessUrl("/");

        http.rememberMe()
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web ->
                web.ignoring()
                        .mvcMatchers("/node_modules/**")
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        );
    }

    // 구현체인 JDBC 기반 토큰 저장소
    // JdbcTokenRepository 가 사용하는 테이블이 있음.
    // 따라서 우리가 사용하는 db에 Impl 에 구현된 스키마가 있어야함
    // 하지만 우리는 Jpa ddl-auto 를 사용하고 있기 때문에, 엔티티 정보를 기반으로 테이블을 알아서 생성해줌
    // 즉, 해당 스키마와 매핑될 엔티티 필요
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // JDBC 기반이어서 DataSource 를 주입해야함.
        // 우리는 JPA 를 쓰고 있기 때문에 DataSource 가 이미 빈으로 등록되어있음
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
}
