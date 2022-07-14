package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserBandRes {
    private int bandIdx;
    private String bandImgUrl;
    private String bandTitle;
    private String bandIntroduction;
    private String bandRegion;
}
