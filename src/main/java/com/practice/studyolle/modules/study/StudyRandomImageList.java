package com.practice.studyolle.modules.study;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StudyRandomImageList {

    private static List<String> images  = Arrays.asList(
            "/images/default_banner.png",
            "/images/default_banner1.jpg",
            "/images/default_banner2.jpg",
            "/images/default_banner3.jpg",
            "/images/default_banner4.jpg",
            "/images/default_banner5.jpg",
            "/images/default_banner6.jpg",
            "/images/default_banner7.jpg"
    );

    public static String getDefaultImage() {
        Random random = new Random();
        int val = random.nextInt(images.size());
        return images.get(val);
    }
}
