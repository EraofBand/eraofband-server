package com.example.demo.src.notice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReportReq {
    private int userIdx;
    private int reportLocation;
    private int reportLocationIdx;
    private String message;
}
