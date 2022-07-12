package com.example.demo.src.pofol;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.pofol.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiOperation;
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


    // 팔로우 한 유저 포트폴리오 리스트 조회
    @ResponseBody
    @GetMapping("")  // (get) https://eraofband.shop/pofol?userIdx=12
    //@ApiOperation(value = " 팔로우 한 유저 포트폴리오 리스트 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<List<GetPofolRes>> getPofol(@RequestParam int userIdx){
        try{
            //jwt 없애기
            //int userIdxByJwt = jwtService.getUserIdx();
            //List<GetPofolRes> getPofol=pofolProvider.retrievePofol(userIdxByJwt);

            List<GetPofolRes> getPofol=pofolProvider.retrievePofol(userIdx);
            return new BaseResponse<>(getPofol);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }




    // 포트폴리오 생성
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/pofol
    @ApiOperation(value = "포트폴리오 생성 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostPofolRes> createPofol(@RequestBody PostPofolReq postPofolReq) {

        if(postPofolReq.getContent().length()>450){
            return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
        }
        if(postPofolReq.getVideoUrl() == null){
           return new BaseResponse<>(POST_POSTS_EMPTY_VIDEOURL);
        }


        try{
            int userIdxByJwt = jwtService.getUserIdx();
            ///if(PostPofolReq.getUserIdx != userIdxByJwt){
            //    return new BaseResponse<>(INVALID_JWT);
           // }
            PostPofolRes postPofolRes = pofolService.createPofol(userIdxByJwt,postPofolReq);
            return new BaseResponse<>(postPofolRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    // 포트폴리오 수정
    @ResponseBody
    @PatchMapping("/{pofolIdx}") // (patch) https://eraofband.shop/pofol/2
    @ApiOperation(value = "포트폴리오 수정 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> modifyPofol(@PathVariable("pofolIdx") int pofolIdx, @RequestBody PatchPofolReq patchPofolReq){
        try{

            if(patchPofolReq.getContent().length()>450)
            {
                return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
            }

            int userIdxByJwt = jwtService.getUserIdx();

            pofolService.modifyPofol(userIdxByJwt, pofolIdx, patchPofolReq);

            String result = "포트폴리오 내용 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 포트폴리오 삭제
    @ResponseBody
    @PatchMapping("/{pofolIdx}/status") // (patch) https://eraofband.shop/pofol/2/status
    @ApiOperation(value = "포트폴리오 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
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


    // 포트폴리오 좋아요
    @ResponseBody
    @PostMapping("/{pofolIdx}/likes") // (post) https://eraofband.shop/pofol/2/likes
    @ApiOperation(value = "포트폴리오 좋아요 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostLikeRes> likesPofol(@PathVariable("pofolIdx") int pofolIdx){
//
        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();


            PostLikeRes postlLikeRes = pofolService.likesPofol(userIdxByJwt,pofolIdx);
            return new BaseResponse<>(postlLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }





    // 포트폴리오 좋아요 취소
    @ResponseBody
    @DeleteMapping ("/{pofolIdx}/unlikes") // (post) https://eraofband.shop/pofol/2/unlikes
    @ApiOperation(value = "포트폴리오 좋아요 취소", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> unlikesPofol(@PathVariable("pofolIdx") int pofolIdx){

        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            pofolService.unlikesPofol(userIdxByJwt,pofolIdx);

            String result = "포트폴리오 좋아요 취소";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

















}
