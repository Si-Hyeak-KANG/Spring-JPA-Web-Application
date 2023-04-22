package com.practice.studyolle.modules.main;

import com.practice.studyolle.modules.account.Account;
import com.practice.studyolle.modules.account.CurrentAccount;
import com.practice.studyolle.modules.study.Study;
import com.practice.studyolle.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    public final StudyRepository studyRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/study")
    public String searchStudy(String keyword, Model model) {
        List<Study> studyList = studyRepository.findByKeyword(keyword);
        model.addAttribute(studyList);
        model.addAttribute("keyword", keyword);
        return "search";
    }
}
