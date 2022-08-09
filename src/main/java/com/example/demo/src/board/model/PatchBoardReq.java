package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchBoardReq {

    private int userIdx;
    private String title;
    private String content;
    private String imgUrl;
}
