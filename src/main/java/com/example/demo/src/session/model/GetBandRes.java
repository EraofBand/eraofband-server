package com.example.demo.src.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetBandRes {
    private int bandIdx;
    private int userIdx;
    private String nickName;
    private String bandTitle;
    private String bandIntroduction;
    private String bandRegion;
    private String bandContent;

    private int vocal;
    private int guitar;
    private int base;
    private int keyboard;
    private int drum;

    private List<GetSessionRes> sessionMembers;

    private String chatRoomLink;
    private String performDate;

    private String bandImgUrl;

    private List<GetSessionRes> applicants;

}
