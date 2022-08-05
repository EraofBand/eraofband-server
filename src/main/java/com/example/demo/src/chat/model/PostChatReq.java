package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostChatReq {

    private int chatRoomIdx;
    private int firstUserIdx;
    private int secondUserIdx;

}
