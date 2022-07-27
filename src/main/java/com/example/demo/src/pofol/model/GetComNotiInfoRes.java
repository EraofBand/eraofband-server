package com.example.demo.src.pofol.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetComNotiInfoRes {
    private int pofolCommentIdx;
    private int pofolIdx;
    private int userIdx;
    private String nickName;
    private String profileImgUrl;
    private String pofolTitle;
}
