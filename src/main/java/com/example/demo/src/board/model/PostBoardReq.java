package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostBoardReq {

    private int userIdx;
    private int category;
    private String content;
    private String title;
    private List<PostImgsUrlReq> postImgsUrl;

}
