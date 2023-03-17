package com.practice.studyolle.settings.validator;

import com.practice.studyolle.account.AccountRepository;
import com.practice.studyolle.domain.Account;
import com.practice.studyolle.settings.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// 빈 객체를 사용하려면, 해당 클래스도 빈이 되어야한다!
@RequiredArgsConstructor
@Component
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;

        // 이미 사용중인 닉네임인지 체크
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if (byNickname != null) {
            errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임을 사용할 수 없습니다.");
        }

    }
}
