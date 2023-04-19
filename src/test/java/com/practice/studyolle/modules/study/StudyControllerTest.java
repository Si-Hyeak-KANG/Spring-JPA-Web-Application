package com.practice.studyolle.modules.study;

import com.practice.studyolle.modules.account.WithAccount;
import com.practice.studyolle.modules.account.AccountRepository;
import com.practice.studyolle.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class StudyControllerTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected StudyService studyService;
    @Autowired protected StudyRepository studyRepository;
    @Autowired protected AccountRepository accountRepository;

    @WithAccount("test")
    @DisplayName("스터디 개설 폼 조회")
    @Test
    void createStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @WithAccount("test")
    @DisplayName("스터디 개설 - 완료")
    @Test
    void createStudy_success() throws Exception {

        mockMvc.perform(post("/new-study")
                        .param("path", "test-path")
                        .param("title", "study title")
                        .param("shortDescription", "short description")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test-path"));

        Study study = studyService.getStudy("test-path");
        assertNotNull(study);
        Account account = accountRepository.findByNickname("test");
        assertTrue(study.getManagers().contains(account));
    }

    @WithAccount("test")
    @DisplayName("스터디 개설 - 실패")
    @Test
    void createStudy_fail() throws Exception {
        // validation error
        mockMvc.perform(post("/new-study")
                        .param("path", "wrong path")
                        .param("title", "study title")
                        .param("shortDescription", "short description")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"));

        Study study = studyRepository.findByPath("test-path");
        assertNull(study);
    }

    @WithAccount("test")
    @DisplayName("스터디 조회")
    @Test
    void viewStudy() throws Exception {
        Study study = new Study();
        study.setPath("test-path");
        study.setTitle("test study");
        study.setShortDescription("short description");
        study.setFullDescription("<p> full description </p>");

        Account account = accountRepository.findByNickname("test");
        studyService.createNewStudy(study, account);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @WithAccount("test")
    @DisplayName("스터디 가입")
    @Test
    void joinStudy() throws Exception {
        Account manager = createAccount("test2");

        Study study = createStudy("test-study", manager);

        mockMvc.perform(get("/study/" + study.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        Account member = accountRepository.findByNickname("test");
        assertTrue(study.getMembers().contains(member));
    }

    @WithAccount("test")
    @DisplayName("스터디 탈퇴")
    @Test
    void leaveStudy() throws Exception {
        Account manager = createAccount("test2");
        Study study = createStudy("test-study", manager);

        Account member = accountRepository.findByNickname("test");
        studyService.addMember(study, member);

        mockMvc.perform(get("/study/" + study.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));
        assertFalse(study.getMembers().contains(member));
    }

    protected Study createStudy(String path, Account manager) {
        Study study = new Study();
        study.setPath(path);
        studyService.createNewStudy(study, manager);
        return study;
    }

    protected Account createAccount(String nickname) {
        Account account = new Account();
        account.setNickname(nickname);
        account.setEmail(nickname + "@email.com");
        accountRepository.save(account);
        return account;
    }

}