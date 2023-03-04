package com.practice.studyolle.account;

import com.practice.studyolle.domain.Account;
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
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


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
    @PostMapping("/sign-up")
    public String signUpSubmit(@ModelAttribute @Valid SignUpForm signUpForm, Errors errors) {

        // JSR 303 검증, @Valid
        if(errors.hasErrors()) {
            return "account/sign-up";
        }
        accountService.processNewAccount(signUpForm);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if(account == null) {
            model.addAttribute("error", "wrong.error");
            return view;
        }

        if (!account.getEmailCheckToken().equals(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }

        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname",account.getNickname());
        return view;
    }


}
