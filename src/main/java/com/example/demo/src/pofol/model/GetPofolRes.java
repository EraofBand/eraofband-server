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
    private String nickName;
    private String profileImgUrl;
    private String title;
    private String content;
    private int pofolLikeCount;
    private int commentCount;
    private String updatedAt;
    private String likeOrNot;
    private String videoUrl;
    private String imgUrl;
}
