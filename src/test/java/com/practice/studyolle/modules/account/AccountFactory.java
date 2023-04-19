package com.practice.studyolle.modules.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountFactory {

    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account newAccount = new Account();
        newAccount.setNickname(nickname);
        newAccount.setEmail(nickname + "@email.com");
        accountRepository.save(newAccount);
        return newAccount;
    }
}
