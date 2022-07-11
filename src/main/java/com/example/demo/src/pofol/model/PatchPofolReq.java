package com.example.demo.src.pofol.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchPofolReq {

    private int userIdx;

    private String title;

    private String content;

}
