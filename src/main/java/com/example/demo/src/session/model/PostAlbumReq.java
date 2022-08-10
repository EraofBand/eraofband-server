package com.example.demo.src.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostAlbumReq {

    private int userIdx;
    private int bandIdx;
    private String albumTitle;
    private String albumImgUrl;
    private String albumDate;

}
