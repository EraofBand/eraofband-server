package com.example.demo.src.lesson.model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetLikesLessonRes {

    private int lessonIdx;
    private String lessonImgUrl;
    private String lessonTitle;
    private String lessonIntroduction;
    private String lessonRegion;
    private int capacity;
    private int memberCount;
}
