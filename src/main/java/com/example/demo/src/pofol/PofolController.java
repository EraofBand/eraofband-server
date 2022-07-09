package com.example.demo.src.pofol;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.pofol.model.GetPofolRes;
import com.example.demo.src.pofol.model.PatchPofolReq;
import com.example.demo.src.pofol.model.PostPofolReq;
import com.example.demo.src.pofol.model.PostPofolRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.POST_POSTS_INVALID_CONTENTS;
import static org.hibernate.sql.InFragment.NULL;
// import static com.example.demo.utils.ValidationRegex.isRegexNickName;


@RestController
@RequestMapping("/pofol")
public class PofolController {

    @Autowired
    private final PofolProvider pofolProvider;

    @Autowired
    private final PofolService pofolService;

    @Autowired
    private final JwtService jwtService;

    public PofolController(PofolProvider pofolProvider, PofolService pofolService, JwtService jwtService){
        this.pofolProvider = pofolProvider;
        this.pofolService = pofolService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetPofolRes>> getPofol(@RequestParam int userIdx){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetPofolRes> getPofol=pofolProvider.retrievePofol(userIdxByJwt);
            //List<GetPofolRes> getPofol=pofolProvider.retrievePofol(userIdx);

            return new BaseResponse<>(getPofol);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    // 포트폴리오 생성
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/pofol
    public BaseResponse<PostPofolRes> createPofol(@RequestBody PostPofolReq postPofolReq) {

        if(postPofolReq.getContent().length()>450){
            return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
        }
        if(postPofolReq.getVideoUrl() == null){
           return new BaseResponse<>(POST_POSTS_EMPTY_VIDEOURL);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            PostPofolRes postPostRes = pofolService.createPofol(userIdxByJwt,postPofolReq);
            return new BaseResponse<>(postPostRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    // 포트폴리오 수정
    @ResponseBody
    @PatchMapping("/{pofolIdx}") // (patch) https://eraofband.shop/pofol/2
    public BaseResponse<String> modifyPofol(@PathVariable("pofolIdx") int pofolIdx, @RequestBody PatchPofolReq patchPofolReq){
        try{

            if(patchPofolReq.getContent().length()>450)
            {
                return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
            }

            int userIdxByJwt = jwtService.getUserIdx();

            pofolService.modifyPofol(userIdxByJwt, pofolIdx, patchPofolReq);
            //pofolService.modifyPofol(patchPofolReq.getUserIdx(),pofolIdx,patchPofolReq);
            String result = "포트폴리오 내용 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 포트폴리오 삭제
    @ResponseBody
    @PatchMapping("/{pofolIdx}/status") // (patch) https://eraofband.shop/pofol/2/status
    public BaseResponse<String> deletePofol(@PathVariable("pofolIdx") int pofolIdx){
        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            pofolService.deletePofol(userIdxByJwt,pofolIdx);

            String result = "포트폴리오가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }












}
