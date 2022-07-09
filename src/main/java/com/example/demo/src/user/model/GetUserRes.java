package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private int userIdx;
    private String nickName;
    private String gender;
    private String birth;
    private String introduction;
    private String profileImgUrl;
    private int session;
    private String region;
    private int followerCount;
    private int followeeCount;
    private int pofolCount;
}
