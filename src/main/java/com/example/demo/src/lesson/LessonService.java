package com.example.demo.src.lesson;

import com.example.demo.config.BaseException;
import com.example.demo.src.GetUserTokenRes;
import com.example.demo.src.SendPushMessage;
import com.example.demo.src.lesson.model.*;
import com.example.demo.src.session.model.GetBandNotiInfoRes;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class LessonService {

    private final LessonDao lessonDao;
    private final LessonProvider lessonProvider;
    private final JwtService jwtService;


    private  int result;


    @Autowired
    public LessonService(LessonDao lessonDao, LessonProvider lessonProvider, JwtService jwtService, ObjectMapper objectMapper) {
        this.lessonDao = lessonDao;
        this.lessonProvider = lessonProvider;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    /**
     *  레슨 생성
     * */
    public PostLessonRes createLesson(int userIdx, PostLessonReq postLessonReq) throws BaseException {

        try{
            int lessonIdx = lessonDao.insertLesson(userIdx, postLessonReq);
            //lessonDao.insertMy(userIdx, lessonIdx);
            return new PostLessonRes(lessonIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     *  레슨 수정
     * */
    public void modifyLesson(int lessonIdx, PatchLessonReq patchLessonReq) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) == 0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }

        try{
            result = lessonDao.updateLesson(lessonIdx, patchLessonReq);

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

        if(result == 0){
            throw new BaseException(MODIFY_FAIL_LESSON);
        }
    }

    /**
     *  레슨 삭제
     * */
    public void deleteLesson(int lessonIdx) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) ==0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }

        try{
            result = lessonDao.updateLessonStatus(lessonIdx);

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(DELETE_FAIL_LESSON);
        }
    }

    private int lessonUserIdx=0;
    /**
     *  레슨 신청
     * */
    public PostSignUpRes applyLesson(int userIdx, int lessonIdx) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) == 0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }

        try{
            lessonUserIdx = lessonDao.insertSignUp(userIdx, lessonIdx);

            //밴드 지원 유저의 정보 얻기
            GetLessonNotiInfoRes getLessonNotiInfoRes=lessonDao.Noti(lessonUserIdx);
            //알림 테이블에 추가
            lessonDao.LessonNoti(getLessonNotiInfoRes);
            return new PostSignUpRes(lessonUserIdx);
        } catch (Exception exception) {
            //System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private final ObjectMapper objectMapper;
    public void sendMessageTo(String title, String body) throws IOException {
        String API_URL = "https://fcm.googleapis.com/v1/projects/eraofband-5bbf4/messages:send";
        //레슨 지원 유저의 정보 얻기
        GetLessonNotiInfoRes getLessonNotiInfoRes=lessonDao.Noti(lessonUserIdx);

        GetUserTokenRes getUserTokenRes= lessonDao.getFCMToken(getLessonNotiInfoRes.getReceiverIdx());
        SendPushMessage sendPushMessage=new SendPushMessage(objectMapper);
        String message = sendPushMessage.makeMessage(getUserTokenRes.getToken(), title, getLessonNotiInfoRes.getNickName()+"님이 회원님의 "+getLessonNotiInfoRes.getLessonTitle()+body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + sendPushMessage.getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        //System.out.println(response.body().string());
    }

    /**
     *  레슨 탈퇴
     * */
    public void withdrawLesson(int userIdx, int lessonIdx) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) == 0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }

        try{
            result = lessonDao.withdrawLesson(userIdx, lessonIdx);

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(WITHDRAW_FAIL_LESSON);
        }
    }

    /**
     *  레슨 좋아요
     * */
    public PostLesLikeRes likesLesson(int userIdx, int lessonIdx) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) == 0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }

        try{
            int result = lessonDao.updateLikes(userIdx, lessonIdx);
            if(result == 0){
                throw new BaseException(LIKES_FAIL_LESSON);
            }
            return new PostLesLikeRes(result);
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     *  레슨 좋아요 취소
     * */
    public void unlikesLesson(int userIdx, int lessonIdx) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) == 0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }

        try{
            result = lessonDao.updateUnlikes(userIdx, lessonIdx);

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(UNLIKES_FAIL_LESSON);
        }
    }
}
