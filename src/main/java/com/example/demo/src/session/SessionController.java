package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.lesson.model.GetLikesLessonRes;
import com.example.demo.src.lesson.model.PostLesLikeRes;
import com.example.demo.src.session.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private final SessionProvider sessionProvider;
    @Autowired
    private final SessionService sessionService;
    @Autowired
    private final JwtService jwtService;

    public SessionController(SessionProvider sessionProvider, SessionService sessionService, JwtService jwtService) {
        this.sessionProvider = sessionProvider;
        this.sessionService = sessionService;
        this.jwtService = jwtService;
    }

    // 최신 밴드
    @ResponseBody
    @GetMapping("/home") // (get) https://eraofband.shop/sessions/home
    @ApiOperation(value = "새로 생성된 밴드 6개 조회")
    public BaseResponse<List<GetNewBandRes>> getNewBand(){
        try{

            List<GetNewBandRes> getNewBandRes = sessionProvider.getNewBand();
            return new BaseResponse<>(getNewBandRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 인기 TOP3 밴드
    @ResponseBody
    @GetMapping("/home/fame") // (get) https://eraofband.shop/sessions/home/fame
    @ApiOperation(value = "인기 TOP3 밴드 정보 반환")
    public BaseResponse<List<GetFameBandRes>> getFameBand(){
        try{

            List<GetFameBandRes> getFameBandRes = sessionProvider.getFameBand();
            return new BaseResponse<>(getFameBandRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 밴드 조회
    @ResponseBody
    @GetMapping("/info/{bandIdx}") // (get) https://eraofband.shop/sessions/2
    @ApiOperation(value = "밴드 정보 반환", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<GetBandRes> getBand(@PathVariable("bandIdx") int bandIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            GetBandRes getBandRes = sessionProvider.getBand(userIdxByJwt, bandIdx);

            return new BaseResponse<>(getBandRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 밴드 생성
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/sessions
    @ApiOperation(value = "밴드 생성 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostBandRes> createBands(@RequestBody PostBandReq postBandReq) {
        if (postBandReq.getBandTitle() == null) {
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if (postBandReq.getBandTitle().length() > 40) {
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if (postBandReq.getBandIntroduction() == null) {
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if (postBandReq.getBandIntroduction().length() > 60) {
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if (postBandReq.getBandRegion() == null) {
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }

        if (postBandReq.getBandContent() == null) {
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if (postBandReq.getBandContent().length() > 3000) {
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if (postBandReq.getBandImgUrl().length() < 1) {
            return new BaseResponse<>(POST_BANDS_EMPTY_IMG);
        }

        try {

            int userIdxByJwt = jwtService.getUserIdx();
            PostBandRes postBandRes = sessionService.createBand(userIdxByJwt, postBandReq);

            return new BaseResponse<>(postBandRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 밴드 수정
    @ResponseBody
    @PatchMapping("/band-info/{bandIdx}") // (patch) https://eraofband.shop/sessions/patch/2
    @ApiOperation(value = "밴드 수정 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> modifyBand(@PathVariable("bandIdx") int bandIdx, @RequestBody PatchBandReq patchBandReq) {
        try {

            if (patchBandReq.getBandContent().length() > 450) {
                return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
            }

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (patchBandReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            sessionService.modifyBand(bandIdx, patchBandReq);

            String result = "밴드 내용 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 밴드 삭제
    @ResponseBody
    @PatchMapping("/status/{bandIdx}") // (patch) https://eraofband.shop/sessions/patch/2/status
    @ApiOperation(value = "밴드 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> deleteBand(@PathVariable("bandIdx") int bandIdx, @RequestBody DeleteBandReq deleteBandReq) {
        try {

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (deleteBandReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            sessionService.deleteBand(bandIdx);

            String result = "밴드가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 밴드 세션 지원 생성
    @ResponseBody
    @PostMapping("/{bandIdx}") // (post) https://eraofband.shop/sessions/apply/2
    @ApiOperation(value = "밴드 세션 지원 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostApplyRes> applySession(@PathVariable("bandIdx") int bandIdx, @RequestBody PostApplyReq postApplyReq) {
        try {

            int userIdxByJwt = jwtService.getUserIdx();
            PostApplyRes postApplyRes = sessionService.applySession(userIdxByJwt, bandIdx, postApplyReq);

            return new BaseResponse<>(postApplyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 세션 지원 수락
    @ResponseBody
    @PatchMapping("/in/{bandIdx}/{userIdx}") // (patch) https://eraofband.shop/sessions/2/accept/9
    @ApiOperation(value = "세션 지원 수락 처리", notes = "path: 해당 페이지의 bandIdx, 지원 수락하려는 userIdx")
    public BaseResponse<String> acceptSession(@PathVariable("bandIdx") int bandIdx, @PathVariable("userIdx") int userIdx) {
        try {

            sessionService.acceptSession(bandIdx, userIdx);

            String result = "세션 지원이 수락되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 세션 지원 거절
    @ResponseBody
    @PatchMapping("/out/{bandIdx}/{userIdx}") // (patch) https://eraofband.shop/sessions/2/reject/9
    @ApiOperation(value = "세션 지원 거절 처리", notes = "path: 해당 페이지의 bandIdx, 지원 거절하려는 userIdx")
    public BaseResponse<String> rejectSession(@PathVariable("bandIdx") int bandIdx, @PathVariable("userIdx") int userIdx) {
        try {
            sessionService.rejectSession(bandIdx, userIdx);

            String result = "세션 지원이 거절되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 밴드 좋아요
     */
    @ResponseBody
    @PostMapping("/likes/{bandIdx}") // (post) https://eraofband.shop/lesson/likes/2
    @ApiOperation(value = "밴드 좋아요 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostBandLikeRes> likesBand(@PathVariable("bandIdx") int bandIdx) {

        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            PostBandLikeRes postBandLikeRes = sessionService.likesBand(userIdxByJwt, bandIdx);

            return new BaseResponse<>(postBandLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 밴드 좋아요 취소
     */
    @ResponseBody
    @DeleteMapping("/unlikes/{bandIdx}") // (delete) https://eraofband.shop/pofol/unlikes/2
    @ApiOperation(value = "밴드 좋아요 취소 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> unlikesBand(@PathVariable("bandIdx") int bandIdx) {

        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
           sessionService.unlikesBand(userIdxByJwt, bandIdx);

            String result = "레슨 좋아요 취소를 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 찜한 밴드 조회
     */
    @ResponseBody
    @GetMapping("/info/likes") // (get) https://eraofband.shop/lesson/info/likes
    @ApiOperation(value = "찜한 밴드 정보 반환", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<List<GetLikesBandRes>> getLikesBand(@PathVariable int userIdx) {
        try {

            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetLikesBandRes> getLikesBandRes = sessionProvider.getLikesBand(userIdxByJwt);
            return new BaseResponse<>(getLikesBandRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 지역-세션 분류 밴드 검색 조회
     */
    @ResponseBody
    @GetMapping("/info/list/{band-region}/{band-session}") // (get) https://eraofband.shop/lesson/info/list/경기/1
    @ApiOperation(value = "지역-세션 분류 밴드 정보 반환")
    public BaseResponse<List<GetInfoBandRes>> getInfoBand(@PathVariable("band-region") String region, @PathVariable("band-session") String session){
        try{

            List<GetInfoBandRes> getInfoBandRes = sessionProvider.getInfoBand(region, session);
            return new BaseResponse<>(getInfoBandRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
