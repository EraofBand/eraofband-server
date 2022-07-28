package com.example.demo.src.session.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetBandNotiInfoRes {
    private int userIdx;
    private int reciverIdx;
    private int bandIdx;
    private String nickName;
    private String profileImgUrl;
    private String bandTitle;
}
