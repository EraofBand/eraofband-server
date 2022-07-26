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
    private String nickName;
    private String profileImgUrl;
    private String introduction;
}
