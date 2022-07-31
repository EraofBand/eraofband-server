package com.example.demo.src.search.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchUserRes {

    private int userIdx;

    private String profileImgUrl;

    private String nickName;

    private int userSession;

    private String token;

    private int follow=0;

}
