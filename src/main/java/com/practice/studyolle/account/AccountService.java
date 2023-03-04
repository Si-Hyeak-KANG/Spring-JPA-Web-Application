package com.practice.studyolle.account;

import com.practice.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account newAccount = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdateByWeb(true)
                .build();

        return accountRepository.save(newAccount);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(newAccount.getEmail());
        mail.setSubject("스터디 올래, 가입 인증 메일");
        mail.setText("/check-email-token?"
                + "token=" + newAccount.getEmailCheckToken()
                + "&email="+ newAccount.getEmail());
        javaMailSender.send(mail);
    }
}
