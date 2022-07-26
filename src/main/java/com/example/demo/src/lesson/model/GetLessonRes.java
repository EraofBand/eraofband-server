package com.example.demo.src.lesson.model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetLessonRes {
    private int lessonIdx;
    private int userIdx;
    private String nickName;
    private String lessonTitle;
    @ApiModelProperty(example="레슨 한 줄 소개")
    private String lessonIntroduction;
    private String lessonRegion;
    @ApiModelProperty(example="레슨 소개 내용")
    private String lessonContent;
    @ApiModelProperty(example="레슨 모집 세션")
    private int lessonSession;

    private List<GetMemberRes> lessonMembers;

    private String chatRoomLink;
    @ApiModelProperty(example="레슨 모집 이미지 URL")
    private String lessonImgUrl;

    @ApiModelProperty(example="Y (레슨 좋아요 여부)")
    private String likeOrNot;
    private int lessonLikeCount;

    private int memberCount;
    private int capacity;


}
