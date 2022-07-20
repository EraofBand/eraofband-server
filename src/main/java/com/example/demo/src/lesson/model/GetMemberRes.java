package com.example.demo.src.lesson.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMemberRes {

    private int session;
    private int userIdx;
    private String nickName;

}
