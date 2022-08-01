package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Users {
    private int userIdx;
    private String nickName;
    private String profileImgUrl;
    private int follow;
}
