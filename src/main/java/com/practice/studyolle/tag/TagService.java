package com.practice.studyolle.tag;

import com.practice.studyolle.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String tagTitle) {
        return tagRepository.findByTitle(tagTitle)
                .orElse(tagRepository.save(
                        Tag.builder().title(tagTitle).build()));
    }
}
