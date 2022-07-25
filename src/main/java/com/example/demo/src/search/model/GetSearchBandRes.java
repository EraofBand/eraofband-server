package com.example.demo.src.search.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchBandRes {

    private int bandIdx;

    private String bandImgUrl;

    private String bandTitle;

    private String bandIntroduction;

    private String bandRegion;

    private int capacity;

    private int memberCount;
}
