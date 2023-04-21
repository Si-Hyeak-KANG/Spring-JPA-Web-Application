package com.practice.studyolle.modules.account;

import com.practice.studyolle.modules.tag.Tag;
import com.practice.studyolle.modules.zone.Zone;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
