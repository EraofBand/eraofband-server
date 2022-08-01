package com.example.demo.src.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String nickName;
    private String birth;
    private String gender;
    private String profileImgUrl;
    private int userSession;
    private String region;
    private String token;
}
