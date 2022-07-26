package com.example.demo.src.lesson.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchLessonReq {

    private int userIdx;

    private String lessonTitle;

    @ApiModelProperty(example="레슨 한 줄 소개")
    private String lessonIntroduction;

    private String lessonRegion;

    @ApiModelProperty(example="레슨 소개 내용")
    private String lessonContent;

    @ApiModelProperty(example="레슨 모집 세션")
    private int lessonSession;

    //private int mySession;

    private int capacity;

    private String chatRoomLink;

    @ApiModelProperty(example="레슨 모집 이미지 URL")
    private String lessonImgUrl;

}
