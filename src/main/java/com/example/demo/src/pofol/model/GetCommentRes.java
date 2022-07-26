package com.example.demo.src.pofol.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {

    private int pofolCommentIdx;

    private int pofolIdx;

    private int userIdx;

    @ApiModelProperty(example="harry (유저 닉네임)")
    private String nickName;

    @ApiModelProperty(example="유저 프로필 이미지 URL")
    private String profileImgUrl;

    @ApiModelProperty(example="포트폴리오 댓글 내용")
    private String content;

    private String updatedAt;
}
