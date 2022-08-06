package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatRoomRes {
    private int chatRoomIdx;
    private String nickName;
    private String profileImgUrl;


}
