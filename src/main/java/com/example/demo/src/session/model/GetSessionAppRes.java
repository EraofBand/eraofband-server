package com.example.demo.src.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSessionAppRes {
    private int buSession;
    private int userIdx;
    private String profileImgUrl;
    private String nickName;
    private String introduction;
    private String updatedAt;
    private String token;
}
