package com.example.demo.src.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetNewBandRes {
    private int bandIdx;
    private String bandRegion;
    private String bandTitle;
    private int sessionNum;
    private int totalNum;
    private String bandImgUrl;

}
