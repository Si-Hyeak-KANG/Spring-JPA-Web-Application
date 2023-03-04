package com.practice.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;

    // 커스텀 검증
    // signUpForm 데이터를 가진 메서드가 실행될 때마다 해당 메서드를 거침
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }


    @GetMapping("/sign-up")
    public String signUpForm(Model model) {

        // 1. 원래는 아래
        // model.addAttribute("signUpForm", new SignUpForm());
        // 2. 클래스의 이름이 키로 사용할 수 있음. 생략 가능
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    // @ModelAttribute 생략 가능
    //
    @PostMapping("/sign-up")
    public String signUpSubmit(@ModelAttribute @Valid SignUpForm signUpForm, Errors errors) {

        // JSR 303 검증, @Valid
        if(errors.hasErrors()) {
            return "account/sign-up";
        }

        // TODO 회원가입처리
        return "redirect:/";
    }
}
