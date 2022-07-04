package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String birth;
    private String gender;
    private String profileImgUrl;
    private int session;
    private String region;
}
