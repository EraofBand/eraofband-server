package com.example.demo.src.pofol.model;

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

    private String content;

    private int pofolLikeCount;

    private int commentCount;

    private String updatedAt;

    private String likeOrNot;

    private String video;

}
