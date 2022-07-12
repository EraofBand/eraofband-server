package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.session.model.GetBandRes;
import com.example.demo.src.session.model.PatchBandReq;
import com.example.demo.src.session.model.PostBandReq;
import com.example.demo.src.session.model.PostBandRes;
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

    public SessionController(SessionProvider sessionProvider, SessionService sessionService, JwtService jwtService){
        this.sessionProvider = sessionProvider;
        this.sessionService = sessionService;
        this.jwtService = jwtService;
    }

    // 밴드 조회
    @ResponseBody
    @GetMapping("/{bandIdx}") // (get) https://eraofband.shop/2
    @ApiOperation(value = "밴드 정보 반환", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<GetBandRes> retrieveBand(@PathVariable("bandIdx") int bandIdx){
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            GetBandRes getBandRes = sessionProvider.retrieveBand(userIdxByJwt, bandIdx);

            return new BaseResponse<>(getBandRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 밴드 생성
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/sessions
    @ApiOperation(value = "밴드 생성 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostBandRes> createBands(@RequestBody PostBandReq postBandReq) {
        if(postBandReq.getBandTitle() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if(postBandReq.getBandTitle().length()>40){
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if(postBandReq.getBandIntroduction() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if(postBandReq.getBandIntroduction().length()>60){
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if(postBandReq.getBandRegion() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }

        if(postBandReq.getBandContent() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if(postBandReq.getBandContent().length()>3000){
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if(postBandReq.getBandImgUrl().length()<1){
            return new BaseResponse<>(POST_BANDS_EMPTY_IMG);
        }

        try{

            int userIdxByJwt = jwtService.getUserIdx();
            PostBandRes postBandRes = sessionService.createBand(userIdxByJwt, postBandReq);

            return new BaseResponse<>(postBandRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 밴드 수정
    @ResponseBody
    @PatchMapping("patch/{bandIdx}") // (patch) https://eraofband.shop/sessions/patch/2
    @ApiOperation(value = "밴드 수정 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> modifyBand(@PathVariable("bandIdx") int bandIdx, @RequestBody PatchBandReq patchBandReq){
        try{

            if(patchBandReq.getBandContent().length()>450)
            {
                return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
            }

            int userIdxByJwt = jwtService.getUserIdx();
            sessionService.modifyBand(userIdxByJwt, bandIdx, patchBandReq);

            String result = "밴드 내용 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 밴드 삭제
    @ResponseBody
    @PatchMapping("patch/{bandIdx}/status") // (patch) https://eraofband.shop/sessions/patch/2/status
    @ApiOperation(value = "밴드 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> deleteBand(@PathVariable("bandIdx") int bandIdx){
        try {

            int userIdxByJwt = jwtService.getUserIdx();
            sessionService.deleteBand(userIdxByJwt,bandIdx);

            String result = "밴드가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
