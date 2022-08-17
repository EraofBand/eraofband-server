package com.example.demo.src.pofol;

import com.example.demo.config.BaseException;
import com.example.demo.src.GetUserTokenRes;
import com.example.demo.src.SendPushMessage;
import com.example.demo.src.lesson.model.GetLessonNotiInfoRes;
import com.example.demo.src.pofol.model.*;
//import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class PofolService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PofolDao pofolDao;
    private final PofolProvider pofolProvider;
    private final JwtService jwtService;

    private int result;


    @Autowired
    public PofolService(PofolDao pofolDao, PofolProvider pofolProvider, JwtService jwtService, ObjectMapper objectMapper) {
        this.pofolDao = pofolDao;
        this.pofolProvider = pofolProvider;
        this.jwtService = jwtService;

        this.objectMapper = objectMapper;
    }

    /**
     * 포트폴리오 생성
     */
    public PostPofolRes createPofol(int userIdx, PostPofolReq postPofolReq) throws BaseException {

        if(pofolProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            int pofolIdx = pofolDao.insertPofol(userIdx, postPofolReq);
            return new PostPofolRes(pofolIdx);
        } catch (Exception exception) {

            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 포트폴리오 수정
     */
    public void modifyPofol(int userIdx, int pofolIdx, PatchPofolReq patchPofolReq) throws BaseException {

        if(pofolProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(pofolProvider.checkPofolExist(pofolIdx) == 0){
            throw new BaseException(POSTS_EMPTY_POFOL_ID);
        }

        try{
            result = pofolDao.updatePofol(pofolIdx,patchPofolReq);
        } catch(Exception exception){

            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(MODIFY_FAIL_POFOL);
        }
    }

    /**
     * 포트폴리오 삭제
     */
    public void deletePofol(int userIdx,int pofolIdx) throws BaseException {

        if(pofolProvider.checkPofolExist(pofolIdx) ==0){
            throw new BaseException(POSTS_EMPTY_POFOL_ID);
        }

        if(pofolProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            result = pofolDao.updatePofolStatus(pofolIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(DELETE_FAIL_POFOL);
        }
    }

    /**
     * 포트폴리오 좋아요
     */
    public PostLikeRes likesPofol(int userIdx, int pofolIdx) throws BaseException {

        if(pofolProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(pofolProvider.checkPofolExist(pofolIdx) == 0){
            throw new BaseException(POSTS_EMPTY_POFOL_ID);
        }

        try{
            result = pofolDao.updateLikes(userIdx, pofolIdx);

            return new PostLikeRes(result);
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 포트폴리오 좋아요 취소
     */
    public void unlikesPofol(int userIdx, int pofolIdx) throws BaseException {

        if(pofolProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(pofolProvider.checkPofolExist(pofolIdx) == 0){
            throw new BaseException(POSTS_EMPTY_POFOL_ID);
        }

        try{
            result = pofolDao.updateUnlikes(userIdx, pofolIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

        if(result == 0){
            throw new BaseException(UNLIKES_FAIL_POFOL);
        }

    }

//    // 댓글 등록
//    public PostCommentRes createComment(int pofolIdx, int userIdx, PostCommentReq postCommentReq) throws BaseException {
//
//        try{
//            int pofolCommentIdx = pofolDao.insertComment(pofolIdx, userIdx, postCommentReq);
//            return new PostCommentRes(pofolCommentIdx);
//        } catch (Exception exception) {
//
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

    private int pofolCommentIdx=0;
    /**
     * 댓글 등록
     */
    public int createComment(int pofolIdx, int userIdx, PostCommentReq postCommentReq) throws BaseException {

        if(pofolProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(pofolProvider.checkPofolExist(pofolIdx) == 0){
            throw new BaseException(POSTS_EMPTY_POFOL_ID);
        }
        if(pofolProvider.checkBlockedUser(pofolIdx, userIdx) == 1){
            throw new BaseException(COMMENT_FAIL_BLOCKED);
        }

        try{
            pofolCommentIdx = pofolDao.insertComment(pofolIdx, userIdx, postCommentReq);
            //포트폴리오 댓글의 정보 얻기
            GetComNotiInfoRes getComNotiInfoRes=pofolDao.Noti(pofolCommentIdx);


            //알림 테이블에 추가
            if(userIdx != getComNotiInfoRes.getReceiverIdx()){
                pofolDao.CommentNoti(getComNotiInfoRes);
            }
            return pofolCommentIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private final ObjectMapper objectMapper;
    public void sendMessageTo(String title, String body) throws IOException {
        String API_URL = "https://fcm.googleapis.com/v1/projects/eraofband-5bbf4/messages:send";
        //포트폴리오 댓글의 정보 얻기
        GetComNotiInfoRes getComNotiInfoRes=pofolDao.Noti(pofolCommentIdx);

        GetUserTokenRes getUserTokenRes= pofolDao.getFCMToken(getComNotiInfoRes.getReceiverIdx());
        SendPushMessage sendPushMessage=new SendPushMessage(objectMapper);
        String message = sendPushMessage.makeMessage(getUserTokenRes.getToken(), title, getComNotiInfoRes.getNickName()+"님이 "+getComNotiInfoRes.getPofolTitle()+body);

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
     * 댓글 삭제
     */
    public void deleteComment(int userIdx, int pofolCommentIdx) throws BaseException {

        if(pofolProvider.checkCommentExist(pofolCommentIdx) == 0){
            throw new BaseException(POSTS_EMPTY_POFOL_COMMENT_ID);
        }

        if(pofolProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            result = pofolDao.deleteComment(pofolCommentIdx);

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(DELETE_FAIL_POFOL_COMMENT);
        }
    }

}
