package com.example.demo.src.notice;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import com.example.demo.src.lesson.model.*;
import com.example.demo.src.notice.model.GetNoticeRes;

import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private final NoticeProvider noticeProvider;
    @Autowired
    private final NoticeService noticeService;
    @Autowired
    private final JwtService jwtService;

    public NoticeController(NoticeProvider noticeProvider, NoticeService noticeService, JwtService jwtService) {
        this.noticeProvider = noticeProvider;
        this.noticeService = noticeService;
        this.jwtService = jwtService;
    }


    /**
     * 알림 리스트 조회 API
     * [GET] /notice/12
     * @return BaseResponse<List<GetNoticeRes>>
     */
    @ResponseBody
    @GetMapping("/notice/{userIdx}")   // (get) https://eraofband.shop/notice/12
    @ApiOperation(value = "알림 리스트 조회")
    @ApiImplicitParam(name="userIdx", value="유저 인덱스", required = true)
    public BaseResponse<List<GetNoticeRes>> getMyNotice(@PathVariable("userIdx") int userIdx){
        try{

            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }

            List<GetNoticeRes> getMyNotice=noticeProvider.getMyNotice(userIdxByJwt);
            noticeService.updateNotice(userIdx);
            return new BaseResponse<>(getMyNotice);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 알림 전체 삭제 API
     * [DELETE] /notice/status
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/status") // (delete) https://eraofband.shop/notice/status
    @ApiOperation(value = "알림 전체 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> deleteNotice() {

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            noticeService.deleteNotice(userIdxByJwt);

            String result = "알림을 모두 삭제했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}
