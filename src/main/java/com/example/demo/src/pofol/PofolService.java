package com.example.demo.src.pofol;

import com.example.demo.config.BaseException;
import com.example.demo.src.pofol.model.*;
//import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class PofolService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PofolDao pofolDao;
    private final PofolProvider pofolProvider;
    private final JwtService jwtService;


    @Autowired
    public PofolService(PofolDao pofolDao, PofolProvider pofolProvider, JwtService jwtService) {
        this.pofolDao = pofolDao;
        this.pofolProvider = pofolProvider;
        this.jwtService = jwtService;

    }

    /**
     * 포트폴리오 생성
     */
    public PostPofolRes createPofol(int userIdx, PostPofolReq postPofolReq) throws BaseException {

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
            int result = pofolDao.updatePofol(pofolIdx,patchPofolReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_POFOL);
            }
        } catch(Exception exception){

            throw new BaseException(DATABASE_ERROR);
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
            int result = pofolDao.updatePofolStatus(pofolIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_POFOL);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
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
            int result = pofolDao.updateLikes(userIdx, pofolIdx);
            if(result == 0){
                throw new BaseException(LIKES_FAIL_POFOL);
            }
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
            int result = pofolDao.updateUnlikes(userIdx, pofolIdx);
            if(result == 0){
                throw new BaseException(UNLIKES_FAIL_POFOL);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
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

    /**
     * 댓글 등록
     */
    public int createComment(int pofolIdx, int userIdx, PostCommentReq postCommentReq) throws BaseException {

        try{
            int pofolCommentIdx = pofolDao.insertComment(pofolIdx, userIdx, postCommentReq);
            return pofolCommentIdx;
        } catch (Exception exception) {

            throw new BaseException(DATABASE_ERROR);
        }
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
            int result = pofolDao.deleteComment(pofolCommentIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_POFOL_COMMENT);
            }
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
