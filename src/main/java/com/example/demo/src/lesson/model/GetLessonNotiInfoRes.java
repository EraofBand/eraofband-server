package com.example.demo.src.lesson.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetLessonNotiInfoRes {
    private int userIdx;
    private int reciverIdx;
    private int lessonIdx;
    private String nickName;
    private String profileImgUrl;
    private String lessonTitle;
}
