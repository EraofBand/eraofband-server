package com.example.demo.src.lesson.model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMemberRes {

    private int mySession;
    private int userIdx;
    @ApiModelProperty(example="레슨 멤버 닉네임")
    private String nickName;
    @ApiModelProperty(example="레슨 멤버 프로필 이미지 URL")
    private String profileImgUrl;
    @ApiModelProperty(example="레슨 멤버 소개글")
    private String introduction;
}
