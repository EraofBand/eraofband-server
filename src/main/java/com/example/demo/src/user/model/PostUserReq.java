package com.example.demo.src.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    @ApiModelProperty(example="harry (유저 닉네임)")
    private String nickName;

    @ApiModelProperty(example="1998-11-13 (유저 생일)")
    private String birth;

    private String gender;
    private String profileImgUrl;
    private int session;
    private String region;
}
