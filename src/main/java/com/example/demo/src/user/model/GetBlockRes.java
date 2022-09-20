package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetBlockRes {
    private int userIdx;
    private String nickName;
    private String profileImgUrl;
    private int blockChecked;
}
