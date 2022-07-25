package com.example.demo.src.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSessionMemRes {
    private int buSession;
    private int userIdx;
    private String profileImgUrl;
    private String nickName;
    private String introduction;
}
