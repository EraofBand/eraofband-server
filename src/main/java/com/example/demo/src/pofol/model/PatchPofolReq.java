package com.example.demo.src.pofol.model;

import io.swagger.annotations.ApiModelProperty;
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
