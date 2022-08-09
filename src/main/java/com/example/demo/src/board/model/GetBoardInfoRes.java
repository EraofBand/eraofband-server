package com.example.demo.src.board.model;

import com.example.demo.src.user.model.GetUserPofolRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetBoardInfoRes {

    private int boardIdx;
    private int userIdx;
    private int category;
    private String title;
    private String imgUrl;
    private String nickName;
    private String content;
    private int boardLikeCount;
    private int commentCount;
    private String createdAt;
    private String likeOrNot;
    private List<GetBoardCommentRes> getBoardComments;
}
