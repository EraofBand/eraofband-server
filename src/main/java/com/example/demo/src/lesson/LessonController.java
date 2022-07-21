package com.example.demo.src.lesson;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.lesson.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    @Autowired
    private final LessonProvider lessonProvider;
    @Autowired
    private final LessonService lessonService;
    @Autowired
    private final JwtService jwtService;

    public LessonController(LessonProvider lessonProvider, LessonService lessonService, JwtService jwtService){
        this.lessonProvider = lessonProvider;
        this.lessonService = lessonService;
        this.jwtService = jwtService;
    }

    // 레슨 조회
    @ResponseBody
    @GetMapping("/info/{lessonIdx}") // (get) https://eraofband.shop/lesson/info/2
    @ApiOperation(value = "레슨 정보 반환", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<GetLessonRes> getLesson(@PathVariable("lessonIdx") int lessonIdx){
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            GetLessonRes getLessonRes = lessonProvider.getLesson(userIdxByJwt, lessonIdx);

            return new BaseResponse<>(getLessonRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    // 레슨 생성
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/lesson
    @ApiOperation(value = "레슨 생성 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostLessonRes> createLesson(@RequestBody PostLessonReq postLessonReq) {
        if(postLessonReq.getLessonTitle() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if(postLessonReq.getLessonTitle().length()>40){
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if(postLessonReq.getLessonIntroduction() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if(postLessonReq.getLessonIntroduction().length()>60){
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if(postLessonReq.getLessonRegion() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }

        if(postLessonReq.getLessonContent() == null){
            return new BaseResponse<>(POST_BANDS_EMPTY_CONTENTS);
        }
        if(postLessonReq.getLessonContent().length()>3000){
            return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
        }

        if(postLessonReq.getLessonImgUrl().length()<1){
            return new BaseResponse<>(POST_BANDS_EMPTY_IMG);
        }

        try{

            int userIdxByJwt = jwtService.getUserIdx();
            PostLessonRes postLessonRes = lessonService.createLesson(userIdxByJwt, postLessonReq);

            return new BaseResponse<>(postLessonRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 레슨 수정
    @ResponseBody
    @PatchMapping("/lesson-info/{lessonIdx}") // (patch) https://eraofband.shop/lesson/patch/2
    @ApiOperation(value = "레슨 수정 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> modifyLesson(@PathVariable("lessonIdx") int lessonIdx, @RequestBody PatchLessonReq patchLessonReq){
        try{

            if(patchLessonReq.getLessonContent().length()>450)
            {
                return new BaseResponse<>(POST_BANDS_INVALID_CONTENTS);
            }

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchLessonReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            lessonService.modifyLesson(lessonIdx, patchLessonReq);

            String result = "레슨 내용 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 레슨 삭제
    @ResponseBody
    @PatchMapping("/status/{lessonIdx}") // (patch) https://eraofband.shop/lesson/patch/2/status
    @ApiOperation(value = "레슨 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> deleteLesson(@PathVariable("lessonIdx") int lessonIdx, @RequestBody PatchLesComReq patchLesComReq){
        try {

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchLesComReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            lessonService.deleteLesson(lessonIdx);

            String result = "레슨이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    // 레슨 신청
    @ResponseBody
    @PostMapping("/{lessonIdx}") // (post) https://eraofband.shop/lesson/apply/2
    @ApiOperation(value = "레슨 신청 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostSignUpRes> applyLesson(@PathVariable("lessonIdx") int lessonIdx) {
        try{

            int userIdxByJwt = jwtService.getUserIdx();
            PostSignUpRes postSignUpRes = lessonService.applyLesson(userIdxByJwt, lessonIdx);

            return new BaseResponse<>(postSignUpRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    // 레슨 좋아요
    @ResponseBody
    @PostMapping("/likes/{lessonIdx}") // (post) https://eraofband.shop/lesson/likes/2
    @ApiOperation(value = "레슨 좋아요 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostLesLikeRes> likesLesson(@PathVariable("lessonIdx") int lessonIdx){

        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            PostLesLikeRes postLesLikeRes = lessonService.likesLesson(userIdxByJwt,lessonIdx);

            return new BaseResponse<>(postLesLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 레슨 좋아요 취소
    @ResponseBody
    @DeleteMapping ("/unlikes/{lessonIdx}") // (delete) https://eraofband.shop/pofol/unlikes/2
    @ApiOperation(value = "레슨 좋아요 취소 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> unlikesLesson(@PathVariable("lessonIdx") int lessonIdx){

        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            lessonService.unlikesLesson(userIdxByJwt,lessonIdx);

            String result = "레슨 좋아요 취소를 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    // 찜한 레슨 조회
    @ResponseBody
    @GetMapping("/info/likes") // (get) https://eraofband.shop/lesson/info/likes
    @ApiOperation(value = "찜한 레슨 정보 반환", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<List<GetLikesLessonRes>> getLikesLesson(@RequestParam int userIdx){
        try{

            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetLikesLessonRes> getLikesLessonRes = lessonProvider.getLikesLesson(userIdxByJwt);
            return new BaseResponse<>(getLikesLessonRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }









}
