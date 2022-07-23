package com.example.demo.src.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetFameBandRes {
    private int bandIdx;
    private String bandTitle;
    private String bandIntroduction;
    private String bandImgUrl;

}
