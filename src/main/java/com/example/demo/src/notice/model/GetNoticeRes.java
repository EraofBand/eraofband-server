package com.example.demo.src.notice.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetNoticeRes {

    private int noticeIdx;
    private String noticeImg;
    private String noticeHead;
    private String noticeBody;
    private String updatedAt;
    private String status;

}
