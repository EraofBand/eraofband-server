package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchUserReq {
    private int userIdx;
    private String nickName;
    private String birth;
    private String gender;
    private String introduction;
    private String profileImgUrl;
    private String region;
}