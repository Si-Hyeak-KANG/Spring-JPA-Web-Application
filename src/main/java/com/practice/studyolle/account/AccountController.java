package com.practice.studyolle.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {

        // 1. 원래는 아래
        // model.addAttribute("signUpForm", new SignUpForm());
        // 2. 클래스의 이름이 키로 사용할 수 있음. 생략 가능
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }
}
