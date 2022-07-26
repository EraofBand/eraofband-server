package com.example.demo.src.pofol.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPofolRes {

    private int pofolIdx;

    private int userIdx;

    @ApiModelProperty(example="harry (유저 닉네임)")
    private String nickName;

    @ApiModelProperty(example="유저 프로필 이미지 URL")
    private String profileImgUrl;

    private String title;

    @ApiModelProperty(example="포트폴리오 게시물 내용")
    private String content;

    private int pofolLikeCount;

    private int commentCount;

    private String updatedAt;

    @ApiModelProperty(example="Y (포트폴리오 좋아요 여부)")
    private String likeOrNot;

    @ApiModelProperty(example="포트폴리오 비디오 URL")
    private String videoUrl;

}
