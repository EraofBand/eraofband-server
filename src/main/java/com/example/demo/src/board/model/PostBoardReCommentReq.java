package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostBoardReCommentReq {

    private int userIdx;
    private String content;
    private int groupNum;

}
