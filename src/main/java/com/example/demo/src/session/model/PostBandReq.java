package com.example.demo.src.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostBandReq {
    private int userIdx;
    private String bandTitle;
    private String bandIntroduction;
    private String bandRegion;
    private String bandContent;
    private int mySession;

    private int vocal;
    private String vocalComment;
    private int guitar;
    private String guitarComment;
    private int base;
    private String baseComment;
    private int keyboard;
    private String keyboardComment;
    private int drum;
    private String drumComment;

    private String chatRoomLink;

    private String bandImgUrl;
}
