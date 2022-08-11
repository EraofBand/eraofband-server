package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetBoardRes {

    private int boardIdx;
    private int userIdx;
    private int category;
    private String title;
    private String nickName;
    private int views;
    private int boardLikeCount;
    private int commentCount;
    private String updatedAt;
}
