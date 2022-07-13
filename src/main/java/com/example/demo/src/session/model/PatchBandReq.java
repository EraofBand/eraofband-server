package com.example.demo.src.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchBandReq {

    private int userIdx;
    private String bandTitle;
    private String bandIntroduction;
    private String bandRegion;
    private String bandContent;

    private int vocal;
    private int guitar;
    private int base;
    private int keyboard;
    private int drum;

    private String chatRoomLink;
    private String performDate;

    private String bandImgUrl;

}
