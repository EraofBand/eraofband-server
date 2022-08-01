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
    private String profileImgUrl;
    private String userIntroduction;
    private String lessonTitle;
    private String lessonIntroduction;
    private String lessonRegion;
    private String lessonContent;
    private int lessonSession;

    private List<GetMemberRes> lessonMembers;

    private String chatRoomLink;
    private String lessonImgUrl;
    private String likeOrNot;
    private int lessonLikeCount;

    private int memberCount;
    private int capacity;

}
