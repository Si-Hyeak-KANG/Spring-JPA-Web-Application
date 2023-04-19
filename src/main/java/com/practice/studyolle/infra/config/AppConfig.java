package com.practice.studyolle.infra.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // default 가 BCrypt
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    /* errors
    1) The destination property com.practice.studyolle.account.Account.setEmail() matches multiple source property hierarchies:

	com.practice.studyolle.settings.form.Notifications.isStudyUpdatedByEmail()
	com.practice.studyolle.settings.form.Notifications.isStudyCreatedByEmail()
	com.practice.studyolle.settings.form.Notifications.isStudyEnrollmentResultByEmail()
    */
    // mapper default 는 None
    // CamelCase 와 UNDERSCORE 를 분리해서 인지
    // 아래 설정으로 UNDERSCORE 가 아니면, 전부 하나의 프로퍼티로 인지
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        return modelMapper;
    }
}
