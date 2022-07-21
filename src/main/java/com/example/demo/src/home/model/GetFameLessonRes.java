package com.example.demo.src.home.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetFameLessonRes {

    private int lessonIdx;

    private String lessonImgUrl;

    private String lessonTitle;

    private String lessonIntroduction;


}
