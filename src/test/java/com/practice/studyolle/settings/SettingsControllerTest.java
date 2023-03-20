package com.practice.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.studyolle.WithAccount;
import com.practice.studyolle.account.AccountRepository;
import com.practice.studyolle.account.AccountService;
import com.practice.studyolle.domain.Account;
import com.practice.studyolle.domain.Tag;
import com.practice.studyolle.domain.Zone;
import com.practice.studyolle.settings.form.TagForm;
import com.practice.studyolle.settings.form.ZoneForm;
import com.practice.studyolle.tag.TagRepository;
import com.practice.studyolle.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.practice.studyolle.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    ObjectMapper objectMapper;

    private Zone testZone = Zone.builder()
            .city("test")
            .localNameOfCity("테스트시")
            .province("테스트주")
            .build();

    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithAccount("test")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE)).andExpect(status().isOk()).andExpect(view().name(SETTINGS + PROFILE)).andExpect(model().attributeExists("account")).andExpect(model().attributeExists("profile"));
    }

    @WithAccount("test")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE).param("bio", bio).with(csrf())).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE)).andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("test");
        assertEquals(bio, account.getBio());
    }

    @WithAccount("test")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "짧은 소개를 수정하는 경우, 35자가 넘었음,,,,길어,길어,길어,길어,길어,길어,길어,길어,길어,길어,길어,길어,길어,길어,길어,길다.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE).param("bio", bio).with(csrf())).andExpect(status().isOk()).andExpect(view().name(SETTINGS + PROFILE)).andExpect(model().attributeExists("account")).andExpect(model().attributeExists("profile")).andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("test");
        assertNull(account.getBio());
    }

    @WithAccount("test")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD)).andExpect(status().isOk()).andExpect(model().attributeExists("account")).andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("패스워드 수정 - 입력값 수정")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD).with(csrf()).param("newPassword", "123456789").param("newPasswordConfirm", "123456789")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD)).andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(passwordEncoder.matches("123456789", test.getPassword()));
    }

    @WithAccount("test")
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD).with(csrf()).param("newPassword", "12345678").param("newPasswordConfirm", "11111111")).andExpect(status().isOk()).andExpect(view().name(SETTINGS + PASSWORD)).andExpect(model().hasErrors()).andExpect(model().attributeExists("passwordForm")).andExpect(model().attributeExists("account"));
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateNickname_form() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT)).andExpect(status().isOk()).andExpect(model().attributeExists("account")).andExpect(model().attributeExists("nicknameForm")).andExpect(view().name(SETTINGS + ACCOUNT));
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정 - 성공")
    @Test
    void updateNickname_success() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT).param("nickname", "test_2").with(csrf())).andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("message")).andExpect(redirectedUrl(ROOT + SETTINGS + ACCOUNT));

        assertFalse(accountRepository.existsByNickname("test"));
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정 - 실패 - 중복 닉네임")
    @Test
    void updateNickname_fail() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT).param("nickname", "test").with(csrf())).andExpect(status().isOk()).andExpect(model().hasErrors()).andExpect(model().attributeExists("account")).andExpect(model().attributeExists("nicknameForm"));

    }

    @WithAccount("test")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + TAGS)).andExpect(view().name(SETTINGS + TAGS)).andExpect(model().attributeExists("account")).andExpect(model().attributeExists("whitelist")).andExpect(model().attributeExists("tags"));
    }

    @WithAccount("test")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                // .content("{\"tagTitle\" : \"newTag\"}") 이렇게 직접 쓸 수도 있음
                .content(objectMapper.writeValueAsString(tagForm)).with(csrf())).andExpect(status().isOk());


        Tag newTag = tagRepository.findByTitle("newTag").get();
        assertNotNull(newTag);
        Account test = accountRepository.findByNickname("test");
        // 갖고온 객체가 detached 상태
        // @Transactional 을 붙여줘서 추가적으로 Lazy loding이 가능해짐
        // ManyToMany 로 연관 매핑된 다른 엔티티의 정보는 안갖고옴.
        assertTrue(test.getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("계정의 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        Account account = accountRepository.findByNickname("test");
        accountService.addTag(account, newTag);

        assertTrue(account.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tagForm)).with(csrf())).andExpect(status().isOk());

        assertFalse(account.getTags().contains(newTag));

    }

    @WithAccount("test")
    @DisplayName("계정의 지역 정보 수정 폼")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES)).andExpect(view().name(SETTINGS + ZONES)).andExpect(model().attributeExists("account")).andExpect(model().attributeExists("whitelist")).andExpect(model().attributeExists("zones"));
    }

    @WithAccount("test")
    @DisplayName("계정에 지역 정보 추가")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Account account = accountRepository.findByNickname("test");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(account.getZones().contains(zone));
    }

    @WithAccount("test")
    @DisplayName("계정의 지역 정보 삭제")
    @Test
    void removeZone() throws Exception {
        Account account = accountRepository.findByNickname("test");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(account, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(account.getZones().contains(zone));
    }
}