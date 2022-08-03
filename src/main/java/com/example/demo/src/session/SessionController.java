package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.lesson.model.GetLikesLessonRes;
import com.example.demo.src.lesson.model.PostLesLikeRes;
import com.example.demo.src.session.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    /**
     * 최신 밴드 조회 API
     * [GET] /sessions/home/new
     * @return BaseResponse<List<GetNewBandRes>>
     */
    @ResponseBody
    @GetMapping("/home/new") // (get) https://eraofband.shop/sessions/home/new
    @ApiOperation(value = "새로 생성된 밴드 6개 조회")
    @ApiResponses({
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetNewBandRes>> getNewBand(){
        try{
            List<GetNewBandRes> getNewBandRes = sessionProvider.getNewBand();
            return new BaseResponse<>(getNewBandRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 인기 밴드 tpo3 조회 API
     * [GET] /sessions/home/fame
     * @return BaseResponse<List<GetFameBandRes>>
     */
    @ResponseBody
    @GetMapping("/home/fame") // (get) https://eraofband.shop/sessions/home/fame
    @ApiOperation(value = "인기 TOP3 밴드 정보 반환")
    @ApiResponses({
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetFameBandRes>> getFameBand(){
        try{

            List<GetFameBandRes> getFameBandRes = sessionProvider.getFameBand();
            return new BaseResponse<>(getFameBandRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 밴드 정보 조회 API
     * [GET] /sessions/info/{bandIdx}
     * @return BaseResponse<GetBandRes>
     */
    @ResponseBody
    @GetMapping("/info/{bandIdx}") // (get) https://eraofband.shop/sessions/info/2
    @ApiOperation(value = "밴드 정보 반환", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="bandIdx", value="조회할 밴드 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetBandRes> getBand(@PathVariable("bandIdx") int bandIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            GetBandRes getBandRes = sessionProvider.getBand(userIdxByJwt, bandIdx);

            return new BaseResponse<>(getBandRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 밴드 생성 API
     * [POST] /sessions
     * @return BaseResponse<PostBandRes>
     */
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/sessions
    @ApiOperation(value = "밴드 생성 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2021, message="내용 입력값을 확인해주세요."),
            @ApiResponse(code=2022, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=2023, message="이미지를 입력해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostBandRes> createBands(@RequestBody PostBandReq postBandReq) {
        //validation 처리
        if (postBandReq.getBandTitle() == null || postBandReq.getBandTitle() == "") {
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if (postBandReq.getBandTitle().length() > 40) {
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if (postBandReq.getBandIntroduction() == null || postBandReq.getBandIntroduction() == "") {
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if (postBandReq.getBandIntroduction().length() > 60) {
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if (postBandReq.getBandRegion() == null || postBandReq.getBandRegion() == "") {
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }

        if (postBandReq.getBandContent() == null || postBandReq.getBandContent() == "") {
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

    /**
     * 밴드 수정 API
     * [PATCH] /sessions/band-info/{bandIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/band-info/{bandIdx}") // (patch) https://eraofband.shop/sessions/band-info/2
    @ApiOperation(value = "밴드 수정 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="bandIdx", value="수정할 밴드 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2020, message="밴드 아이디 값을 확인해주세요."),
            @ApiResponse(code=2021, message="내용 입력값을 확인해주세요."),
            @ApiResponse(code=2022, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=2023, message="이미지를 입력해주세요."),
            @ApiResponse(code=2024, message="밴드 변경에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> modifyBand(@PathVariable("bandIdx") int bandIdx, @RequestBody PatchBandReq patchBandReq) {
        try {

            if (patchBandReq.getBandTitle() == null || patchBandReq.getBandTitle() == "") {
                return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
            }
            if (patchBandReq.getBandTitle().length() > 40) {
                return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
            }

            if (patchBandReq.getBandIntroduction() == null || patchBandReq.getBandIntroduction() == "") {
                return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
            }
            if (patchBandReq.getBandIntroduction().length() > 60) {
                return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
            }

            if (patchBandReq.getBandRegion() == null || patchBandReq.getBandRegion() == "") {
                return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
            }

            if (patchBandReq.getBandContent() == null || patchBandReq.getBandContent() == "") {
                return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
            }
            if (patchBandReq.getBandContent().length() > 3000) {
                return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
            }

            if (patchBandReq.getBandImgUrl().length() < 1) {
                return new BaseResponse<>(POST_BANDS_EMPTY_IMG);
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

    /**
     * 밴드 삭제 API
     * [PATCH] /sessions/status/{bandIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{bandIdx}") // (patch) https://eraofband.shop/sessions/status/2
    @ApiOperation(value = "밴드 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="bandIdx", value="삭제할 밴드 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2020, message="밴드 아이디 값을 확인해주세요."),
            @ApiResponse(code=2025, message="밴드 삭제에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
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

    /**
     * 밴드 세션 지원 API
     * [POST] /sessions/{bandIdx}
     * @return BaseResponse<PostApplyRes>
     */
    @ResponseBody
    @PostMapping("/{bandIdx}") // (post) https://eraofband.shop/sessions/2
    @ApiOperation(value = "밴드 세션 지원 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="bandIdx", value="지원할 밴드 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2020, message="밴드 아이디 값을 확인해주세요."),
            @ApiResponse(code=2030, message="이미 지원한 밴드입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostApplyRes> applySession(@PathVariable("bandIdx") int bandIdx, @RequestBody PostApplyReq postApplyReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(sessionProvider.checkBandSession(userIdxByJwt, bandIdx) == 1 || sessionProvider.checkBandApply(userIdxByJwt, bandIdx)==1){
                throw new BaseException(ALREADY_BAND);
            }

            PostApplyRes postApplyRes = sessionService.applySession(userIdxByJwt, bandIdx, postApplyReq);

            return new BaseResponse<>(postApplyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 밴드 탈퇴 API
     * [DELETE] /sessions/out/{bandIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping ("/out/{bandIdx}") // (delete) https://eraofband.shop/sessions/out/2
    @ApiOperation(value = "밴드 탈퇴 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="bandIdx", value="탈퇴할 밴드 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2020, message="밴드 아이디 값을 확인해주세요."),
            @ApiResponse(code=2031, message="등록하지 않은 밴드입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> withdrawBand(@PathVariable("bandIdx") int bandIdx){

        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            if(sessionProvider.checkBandSession(userIdxByJwt, bandIdx) == 0){
                throw new BaseException(NOT_BAND_MEMBER);
            }
            sessionService.withdrawBand(userIdxByJwt,bandIdx);

            String result = "밴드에서 탈퇴 처리되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    /**
     * 세션 지원 수락 API
     * [PATCH] /sessions/in/{bandIdx}/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/in/{bandIdx}/{userIdx}") // (patch) https://eraofband.shop/sessions/in/1/2
    @ApiOperation(value = "세션 지원 수락 처리", notes = "path: 해당 페이지의 bandIdx, 지원 수락하려는 userIdx")
    @ApiImplicitParams({
            @ApiImplicitParam(name="bandIdx", value="지원한 밴드 인덱스", required = true),
            @ApiImplicitParam(name="userIdx", value="지원한 유저 인덱스", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2020, message="밴드 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> acceptSession(@PathVariable("bandIdx") int bandIdx, @PathVariable("userIdx") int userIdx) {
        try {

            sessionService.acceptSession(bandIdx, userIdx);

            String result = "세션 지원이 수락되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 세션 지원 거절 API
     * [PATCH] /sessions/out/{bandIdx}/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/out/{bandIdx}/{userIdx}") // (patch) https://eraofband.shop/sessions/out/1/2
    @ApiOperation(value = "세션 지원 거절 처리", notes = "path: 해당 페이지의 bandIdx, 지원 거절하려는 userIdx")
    @ApiImplicitParams({
            @ApiImplicitParam(name="bandIdx", value="지원한 밴드 인덱스", required = true),
            @ApiImplicitParam(name="userIdx", value="지원한 유저 인덱스", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2020, message="밴드 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
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
     * 밴드 좋아요 API
     * [POST] /sessions/likes/{bandIdx}
     * @return BaseResponse<PostBandLikeRes>
     */
    @ResponseBody
    @PostMapping("/likes/{bandIdx}") // (post) https://eraofband.shop/sessions/likes/2
    @ApiOperation(value = "밴드 좋아요 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="bandIdx", value="밴드 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2020, message="밴드 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
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
     * 밴드 좋아요 취소 API
     * [DELETE] /sessions/unlikes/{bandIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/unlikes/{bandIdx}") // (delete) https://eraofband.shop/sessions/unlikes/2
    @ApiOperation(value = "밴드 좋아요 취소 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="bandIdx", value="밴드 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2020, message="밴드 아이디 값을 확인해주세요."),
            @ApiResponse(code=2027, message="밴드 좋아요 취소에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
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
     * 찜한 밴드 정보 반환 API
     * [GET] /sessions/info/likes
     * @return BaseResponse<List<GetLikesBandRes>>
     */
    @ResponseBody
    @GetMapping("/info/likes") // (get) https://eraofband.shop/sessions/info/likes
    @ApiOperation(value = "찜한 밴드 정보 반환", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetLikesBandRes>> getLikesBand() {
        try {

            int userIdxByJwt = jwtService.getUserIdx();

            List<GetLikesBandRes> getLikesBandRes = sessionProvider.getLikesBand(userIdxByJwt);
            return new BaseResponse<>(getLikesBandRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 지역-세션 분류 밴드 검색 조회 API
     * [GET] /sessions/info/list/{band-region}/{band-session}
     * @return BaseResponse<List<GetInfoBandRes>>
     */
    @ResponseBody
    @GetMapping("/info/list/{band-region}/{band-session}") // (get) https://eraofband.shop/sessions/info/list/경기도/1
    @ApiOperation(value = "지역-세션 분류 밴드 정보 반환")
    @ApiImplicitParams({
            @ApiImplicitParam(name="band-region", value="밴드 지역(서울, 경기도, 전체)", required = true),
            @ApiImplicitParam(name="band-session", value="밴드 세션(0, 1, 2, 3, 4, 5)", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetInfoBandRes>> getInfoBand(@PathVariable("band-region") String region, @PathVariable("band-session") int session){
        try{

            List<GetInfoBandRes> getInfoBandRes = sessionProvider.getInfoBand(region, session);
            return new BaseResponse<>(getInfoBandRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
