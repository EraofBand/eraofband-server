package com.example.demo.src.lesson.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMemberRes {

    private int mySession;
    private int userIdx;
    private String nickName;

}
