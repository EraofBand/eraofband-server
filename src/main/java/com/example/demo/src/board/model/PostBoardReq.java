package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostBoardReq {

    private int userIdx;
    private int category;
    private String content;
    private String imgUrl;
    private String title;

}
