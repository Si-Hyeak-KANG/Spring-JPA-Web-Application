package com.practice.studyolle.modules.study;

import com.practice.studyolle.infra.AbstractContainerBaseTest;
import com.practice.studyolle.infra.MockMvcTest;
import com.practice.studyolle.modules.account.Account;
import com.practice.studyolle.modules.account.AccountFactory;
import com.practice.studyolle.modules.account.AccountRepository;
import com.practice.studyolle.modules.account.WithAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class StudySettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    StudyFactory studyFactory;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    StudyRepository studyRepository;

    @WithAccount("test")
    @DisplayName("스터디 소개 수정 폼 조회 - 실패 (권한 없는 유저)")
    @Test
    void updateDescriptionForm() throws Exception {
        Account manager = accountFactory.createAccount("test2");
        Study study = studyFactory.createStudy("test-study", manager);

        mockMvc.perform(
                        get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @WithAccount("test")
    @DisplayName("스터디 소개 수정 폼 조회 - 성공")
    @Test
    void updateDescriptionForm_success() throws Exception {
        Account manager = accountRepository.findByNickname("test");
        Study study = studyFactory.createStudy("test-study", manager);

        mockMvc.perform(
                        get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @WithAccount("test")
    @DisplayName("스터디 소개 수정 - 성공")
    @Test
    void updateDescription_success() throws Exception {
        Account manager = accountRepository.findByNickname("test");
        Study study = studyFactory.createStudy("test-study", manager);

        String url = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("shortDescription", "short description")
                        .param("fullDescription", "full description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url))
                .andExpect(flash().attributeExists("message"));
    }

    @WithAccount("test")
    @DisplayName("스터디 소개 수정 - 실패 (짧은 소개 미입력)")
    @Test
    void updateDescription_fail() throws Exception {
        Account manager = accountRepository.findByNickname("test");
        Study study = studyFactory.createStudy("test-study", manager);

        String url = "/study/" + study.getPath() + "/settings/description";

        mockMvc.perform(post(url)
                .with(csrf())
                .param("shortDescription", "")
                .param("fullDescription", "full description"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }

}
