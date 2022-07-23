package com.example.demo.src.session.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetInfoBandRes {

    private int bandIdx;

    private String bandImgUrl;

    private String bandTitle;

    private String bandIntroduction;

    private String bandRegion;

    private int capacity;

    private int memberCount;
}
