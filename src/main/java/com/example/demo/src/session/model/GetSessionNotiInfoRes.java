package com.example.demo.src.session.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSessionNotiInfoRes {
    // 받는 유저 인덱스
    private int userIdx;
    private String bandTitle;
    private String bandImgUrl;
}
