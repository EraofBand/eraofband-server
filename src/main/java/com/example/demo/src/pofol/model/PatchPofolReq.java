package com.example.demo.src.pofol.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchPofolReq {

    private int userIdx;

    private String title;

    @ApiModelProperty(example="수정된 포트폴리오 게시물 내용")
    private String content;

}
