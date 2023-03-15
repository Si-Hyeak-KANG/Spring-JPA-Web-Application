package com.practice.studyolle.settings;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm)target;
        if (!isSameNewPassword(passwordForm)) {
            errors.rejectValue("newPassword", "wrong.value", "입력한 패스워드가 일치하지 않습니다.");
        }
    }

    private boolean isSameNewPassword(PasswordForm passwordForm) {
        return passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm());
    }
}
