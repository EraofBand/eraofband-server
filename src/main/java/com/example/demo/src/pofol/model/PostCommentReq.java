package com.example.demo.src.pofol.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCommentReq {

    private int userIdx;

    @ApiModelProperty(example="포트폴리오 댓글 내용")
    private String content;

}
