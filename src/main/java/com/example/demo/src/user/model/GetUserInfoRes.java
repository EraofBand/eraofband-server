package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {
    private int userIdx;
    private String nickName;
    private String gender;
    private String birth;
    private String introduction;
    private String profileImgUrl;
    private int userSession;
    private String region;
    private int followerCount;
    private int followeeCount;
    private int pofolCount;
    private int follow=0;
}
