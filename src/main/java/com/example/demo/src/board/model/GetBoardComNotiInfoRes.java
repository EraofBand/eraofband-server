package com.example.demo.src.board.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetBoardComNotiInfoRes {
    private int boardCommentIdx;
    private int receiverIdx;
    private int boardIdx;
    private int userIdx;
    private String nickName;
    private String profileImgUrl;
    private String title;

}
