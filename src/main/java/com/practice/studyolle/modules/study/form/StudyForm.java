package com.practice.studyolle.modules.study.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StudyForm {

    public static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}";

    @NotBlank
    @Length(min = 2, max = 20, message = "2자 이상 20자 이하로 작성해주세요.")
    @Pattern(regexp = VALID_PATH_PATTERN, message = "공백없이 [한글, 숫자, 영문, 하이픈(-)과 언더바(_)]로 이루어진 문자만 사용가능합니다.")
    private String path;

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;
}
