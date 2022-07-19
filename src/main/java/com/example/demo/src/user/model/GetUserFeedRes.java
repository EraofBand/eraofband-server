package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedRes {
    private GetUserInfoRes getUser;
    private List<GetUserPofolRes> getUserPofol;
    private List<GetUserBandRes> getUserBand;
}
