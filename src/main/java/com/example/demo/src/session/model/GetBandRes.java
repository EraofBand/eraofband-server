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
    private String profileImgUrl;
    private String userIntroduction;
    private String bandTitle;
    private String bandIntroduction;
    private String bandRegion;
    private String bandContent;

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

    private List<GetSessionMemRes> sessionMembers;

    private String chatRoomLink;
    private String performTitle;
    private String performDate;
    private String performTime;
    private String performLocation;
    private int performFee;
    private String bandImgUrl;
    private String likeOrNot;
    private int bandLikeCount;
    private int capacity;
    private int memberCount;

    private List<GetSessionAppRes> applicants;

    private String token;

}
