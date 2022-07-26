package com.example.demo.src.lesson.model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetInfoLessonRes {

    private int lessonIdx;

    @ApiModelProperty(example="레슨 모집 이미지 URL")
    private String lessonImgUrl;

    private String lessonTitle;

    @ApiModelProperty(example="레슨 한 줄 소개")
    private String lessonIntroduction;

    private String lessonRegion;

    private int capacity;

    private int memberCount;
}
