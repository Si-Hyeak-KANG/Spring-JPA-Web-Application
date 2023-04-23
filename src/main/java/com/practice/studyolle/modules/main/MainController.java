package com.practice.studyolle.modules.main;

import com.practice.studyolle.modules.account.Account;
import com.practice.studyolle.modules.account.CurrentAccount;
import com.practice.studyolle.modules.study.Study;
import com.practice.studyolle.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    public final StudyRepository studyRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        model.addAttribute("studyList", studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Pageable -> size, page, sort 파라미터를 받을 수 있음
    @GetMapping("/search/study")
    public String searchStudy(String keyword, Model model,
            @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);

        // Spring MVC
        // 이름을 주면서 넘기는 거와 안주는 것에 차이가 있음
        // 만약 empty 컬렉션을 이름 없이 넘길 경우. 무시해버림.
        model.addAttribute("studyPage",studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }
}
