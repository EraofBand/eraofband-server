package com.example.demo.src.pofol.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class PostPofolReq {

    private int userIdx;

    private String content;

    private String videoUrl;

    private String title;
}
