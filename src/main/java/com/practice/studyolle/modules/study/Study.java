package com.practice.studyolle.modules.study;

import com.practice.studyolle.modules.account.UserAccount;
import com.practice.studyolle.modules.account.Account;
import com.practice.studyolle.modules.tag.Tag;
import com.practice.studyolle.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static com.practice.studyolle.modules.study.StudyRandomImageList.getDefaultImage;

/*
// Eager fetch 엔티티 그래프 이름으로 정의
@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")
})
*/
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    private boolean defaultImage;

    @ManyToMany
    private Set<Tag> tags;

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public boolean isManagerOf(Account account) {
        return this.getManagers().contains(account);
    }

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account)
                && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains((userAccount.getAccount()));
    }

    public void setImage(String image) {
        this.image = image;
        this.setDefaultImage(false);
    }

    public void setImageDefault() {
        this.setImage(getDefaultImage());
        setDefaultImage(true);
    }

    public void publish() {
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("스터디를 이미 공개했거나 종료했습니다.");
        }
    }

    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
            this.published = false;
        } else {
            throw new RuntimeException("스터디를 공개하지 않았거나 이미 종료한 상태입니다.");
        }
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("스터디를 공개하거나 30분 뒤 다시 시도하세요.");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("스터디를 공개하거나 30분 뒤 다시 시도하세요.");
        }
    }

    public boolean canUpdateRecruiting() {
        return this.published
                && this.recruitingUpdateTime == null
                || this.recruitingUpdateTime.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    public boolean isRemovable() {
        return !this.published; // TODO 모임을 했던 스터디는 삭제할 수 없다.
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }
}
