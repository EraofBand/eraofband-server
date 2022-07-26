package com.example.demo.src.pofol.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class PostPofolReq {

    private int userIdx;

    @ApiModelProperty(example="포트폴리오 게시물 내용")
    private String content;

    @ApiModelProperty(example="포트폴리오 비디오 URL")
    private String videoUrl;

    private String title;

    @ApiModelProperty(example="포트폴리오 이미지 URL")
    private String imgUrl;

}
