package com.example.demo.src.lesson.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchLessonReq {

    private int userIdx;

    private String lessonTitle;

    private String lessonIntroduction;

    private String lessonRegion;

    private String lessonContent;

    private int lessonSession;

    //private int mySession;

    private int capacity;

    private String chatRoomLink;

    private String lessonImgUrl;

}
