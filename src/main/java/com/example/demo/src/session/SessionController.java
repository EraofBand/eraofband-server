package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.session.model.PostBandReq;
import com.example.demo.src.session.model.PostBandRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.POST_BANDS_EMPTY_CONTENTS;
import static com.example.demo.config.BaseResponseStatus.POST_BANDS_EMPTY_IMGRUL;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private final SessionProvider sessionProvider;
    @Autowired
    private final SessionService sessionService;
    @Autowired
    private final JwtService jwtService;

    public SessionController(SessionProvider sessionProvider, SessionService sessionService, JwtService jwtService){
        this.sessionProvider = sessionProvider;
        this.sessionService = sessionService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostBandRes> createBands(@RequestBody PostBandReq postBandReq) {
        if(postBandReq.getBandContent() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if(postBandReq.getBandContent().length()>3000){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if(postBandReq.getBandImgUrl().length()<1){
            return new BaseResponse<>(POST_BANDS_EMPTY_IMGRUL);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            PostBandRes postBandRes = sessionService.createBand(userIdxByJwt, postBandReq);
            return new BaseResponse<>(postBandRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
