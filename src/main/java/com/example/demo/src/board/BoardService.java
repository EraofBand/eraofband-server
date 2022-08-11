package com.example.demo.src.board;
import com.example.demo.config.BaseException;
import com.example.demo.src.GetUserTokenRes;
import com.example.demo.src.SendPushMessage;
import com.example.demo.src.board.model.*;
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
public class BoardService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BoardDao boardDao;
    private final BoardProvider boardProvider;
    private final JwtService jwtService;

    private int result;


    @Autowired
    public BoardService(BoardDao boardDao, BoardProvider boardProvider, JwtService jwtService, ObjectMapper objectMapper) {
        this.boardDao = boardDao;
        this.boardProvider = boardProvider;
        this.jwtService = jwtService;

        this.objectMapper = objectMapper;
    }

    /**
     * 게시판 게시물 생성
     */
    public PostBoardRes createBoard(int userIdx, PostBoardReq postBoardReq) throws BaseException {

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            int boardIdx = boardDao.insertBoard(userIdx, postBoardReq);
            return new PostBoardRes(boardIdx);
        } catch (Exception exception) {

            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시판 게시물 수정
     */
    public void modifyBoard(int userIdx, int boardIdx, PatchBoardReq patchBoardReq) throws BaseException {

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(boardProvider.checkBoardExist(boardIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }

        if(patchBoardReq.getTitle() == null || patchBoardReq.getTitle() == ""){
            throw new BaseException(POST_BOARD_EMPTY_TITLE);
        }

        try{
            result = boardDao.updateBoard(boardIdx,patchBoardReq);
        } catch(Exception exception){

            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(MODIFY_FAIL_BOARD);
        }
    }

    /**
     * 게시판 게시물 삭제
     */
    public void deleteBoard(int userIdx,int boardIdx) throws BaseException {

        if(boardProvider.checkBoardExist(boardIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            result = boardDao.updateBoardStatus(boardIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(DELETE_FAIL_BOARD);
        }
    }

    /**
     * 게시물 조회 수 증가
     */
    public void addViewCount(int boardIdx) throws BaseException {

        if(boardProvider.checkBoardExist(boardIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }

        try{
            result = boardDao.updateBoardCount(boardIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(ADD_FAIL_BOARD);
        }
    }

    private int boardCommentIdx=0;

    /**
     * 댓글 등록
     */
    public int createComment(int boardIdx, int userIdx, PostBoardCommentReq postBoardCommentReq) throws BaseException {

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(boardProvider.checkBoardExist(boardIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }

        try{
            boardCommentIdx = boardDao.insertComment(boardIdx, userIdx, postBoardCommentReq);
            //게시글 댓글의 정보 얻기
            GetBoardComNotiInfoRes getBoardComNotiInfoRes =boardDao.Noti(boardCommentIdx);


            //알림 테이블에 추가
            if(userIdx != getBoardComNotiInfoRes.getReceiverIdx()){
                boardDao.CommentNoti(getBoardComNotiInfoRes);
            }
            return boardCommentIdx;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private final ObjectMapper objectMapper;
    public void sendMessageTo(String title, String body) throws IOException {
        String API_URL = "https://fcm.googleapis.com/v1/projects/eraofband-5bbf4/messages:send";
        //포트폴리오 댓글의 정보 얻기
        GetBoardComNotiInfoRes getBoardComNotiInfoRes =boardDao.Noti(boardCommentIdx);

        GetUserTokenRes getUserTokenRes= boardDao.getFCMToken(getBoardComNotiInfoRes.getReceiverIdx());
        SendPushMessage sendPushMessage=new SendPushMessage(objectMapper);
        String message = sendPushMessage.makeMessage(getUserTokenRes.getToken(), title, getBoardComNotiInfoRes.getNickName()+"님이 "+getBoardComNotiInfoRes.getTitle()+body);

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

    public void sendReMessageTo(String title, String body, PostBoardCommentReq postBoardCommentReq) throws IOException {
        String API_URL = "https://fcm.googleapis.com/v1/projects/eraofband-5bbf4/messages:send";
        //포트폴리오 댓글의 정보 얻기
        GetBoardComNotiInfoRes getBoardComNotiInfoRes =boardDao.NotiRe(boardCommentIdx, postBoardCommentReq.getGroupNum());

        GetUserTokenRes getUserTokenRes= boardDao.getFCMToken(getBoardComNotiInfoRes.getReceiverIdx());
        SendPushMessage sendPushMessage=new SendPushMessage(objectMapper);
        String message = sendPushMessage.makeMessage(getUserTokenRes.getToken(), title, getBoardComNotiInfoRes.getNickName()+"님이 "+body);

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
     * 댓글 그룹 추가
     */
    public void addGroupNum(int boardCommentIdx) throws BaseException {

        if(boardProvider.checkCommentExist(boardCommentIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_COMMENT_ID);
        }
        try{
            result = boardDao.insertCommentGroup(boardCommentIdx);

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 대댓글 등록
     */
    private int boardReCommentIdx=0;
    public int createReComment(int boardIdx, int userIdx, PostBoardCommentReq postBoardCommentReq) throws BaseException {

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(boardProvider.checkBoardExist(boardIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }

        if(boardProvider.checkCommentExist(postBoardCommentReq.getGroupNum()) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_COMMENT_ID);
        }

        try{
            boardReCommentIdx = boardDao.insertReComment(boardIdx, userIdx, postBoardCommentReq);
            //게시글 댓글의 정보 얻기
            GetBoardComNotiInfoRes getBoardComNotiInfoRes =boardDao.NotiRe(boardReCommentIdx, postBoardCommentReq.getGroupNum());


            //알림 테이블에 추가
            if(userIdx != getBoardComNotiInfoRes.getReceiverIdx()){
                boardDao.CommentReNoti(getBoardComNotiInfoRes);
            }
            return boardReCommentIdx;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 댓글 삭제
     */
    public void deleteComment(int userIdx, int boardCommentIdx) throws BaseException {

        if(boardProvider.checkCommentExist(boardCommentIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_COMMENT_ID);
        }

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        if(boardProvider.checkReplyExist(boardCommentIdx) == 1){
            throw new BaseException(DELETE_FAIL_BOARD_COMMENT_REPLY);
        }

        try{
            result = boardDao.deleteComment(boardCommentIdx);

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(DELETE_FAIL_BOARD_COMMENT);
        }
    }

    /**
     * 게시물 좋아요
     */
    public PostBoardLikeRes likesBoard(int userIdx, int boardIdx) throws BaseException {

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(boardProvider.checkBoardExist(boardIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }
        if(boardProvider.checkBoardLiked(userIdx, boardIdx) == 1){
            throw new BaseException(DUPLICATED_BOARD_LIKE);
        }

        try{
            result = boardDao.updateLikes(userIdx, boardIdx);

            return new PostBoardLikeRes(result);
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
