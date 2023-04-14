package com.practice.studyolle.event;

import com.practice.studyolle.account.CurrentAccount;
import com.practice.studyolle.domain.Account;
import com.practice.studyolle.domain.study.Study;
import com.practice.studyolle.event.form.EventForm;
import com.practice.studyolle.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;

    @GetMapping("/new-event")
    public String newEventFrom(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }
}
