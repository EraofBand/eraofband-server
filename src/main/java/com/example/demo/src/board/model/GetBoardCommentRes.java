package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetBoardCommentRes {
    private int boardCommentIdx;
    private int boardIdx;
    private int userIdx;
    private String nickName;
    private String profileImgUrl;
    private String content;
    private String updatedAt;
}
