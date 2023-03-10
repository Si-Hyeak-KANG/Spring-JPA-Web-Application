package com.practice.studyolle.settings;

import com.practice.studyolle.account.AccountService;
import com.practice.studyolle.account.CurrentUser;
import com.practice.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";
    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    // Errors : ModelAttribute 바인딩시 발생하는 에러 또는 validation 위반
    // ModelAttribute Object : 스프링 프레임워크가 해당 객체를 인스턴스를 먼저 생성하고, setter 로 주입
    // 따라서 위 객체에 default 생성자가 없다면 NullPointException 발생
    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile,
                                Errors errors, Model model, RedirectAttributes attributes) {

        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);

        // SpringMVC 에서 제공하는 기능
        // 리다이렉트 때 한번만 쓸 데이터로, 리다이렉트 시 Model 에 저장되었다가 사용 이후 사라짐.
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        // 사용자가 새로고침을 하여도, form submit 이 다시 발생하지 않도록 리다이렉트
        return "redirect:" + SETTINGS_PROFILE_URL;
    }
}
