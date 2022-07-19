package com.example.demo.src.pofol;


import com.example.demo.config.BaseException;

import com.example.demo.src.pofol.model.GetCommentRes;

import com.example.demo.src.pofol.model.GetPofolRes;
import com.example.demo.src.pofol.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class PofolProvider {

    private final PofolDao pofolDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PofolProvider(PofolDao pofolDao, JwtService jwtService) {
        this.pofolDao = pofolDao;
        this.jwtService = jwtService;
    }


    // 유저 확인
    public int checkUserExist(int userIdx) throws BaseException{
        try{
            return pofolDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 포트폴리오 확인
    public int checkPofolExist(int pofolIdx) throws BaseException{
        try{
            return pofolDao.checkPofolExist(pofolIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkCommentExist(int pofolCommentIdx) throws BaseException{
        try{
            return pofolDao.checkCommentExist(pofolCommentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 유저의 포트폴리오인지 확인
    public int checkUserPofolExist(int userIdx,int pofolIdx) throws BaseException{
        try{
            return pofolDao.checkUserPofolExist(userIdx,pofolIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 이메일 확인
    public int checkEmailExist(String email) throws BaseException{
        try{
            return pofolDao.checkEmailExist(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 이메일 확인
    public String checkUserStatus(String email) throws BaseException{
        try{
            return pofolDao.checkUserStatus(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 포트폴리오 리스트 조회
    public List<GetPofolRes> retrievePofol(int userIdx) throws BaseException {

        if(checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            List<GetPofolRes> getPofol = pofolDao.selectPofol(userIdx);
            return getPofol;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 내 포트폴리오 조회
    public List<GetPofolRes> retrieveMyPofol(int userIdx) throws BaseException {

        if(checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            List<GetPofolRes> getMyPofol = pofolDao.selectMyPofol(userIdx);
            return getMyPofol;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 특정 댓글 조회
    public GetCommentRes certainComment (int pofolCommentIdx) throws BaseException {

        try{
            GetCommentRes getCommentRes = pofolDao.certainComment(pofolCommentIdx);
            return getCommentRes;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }


    }

    // 포트폴리오 댓글 리스트 조회
    public List<GetCommentRes> retrieveComment(int pofolIdx) throws BaseException {

        if(checkPofolExist(pofolIdx) ==0){
            throw new BaseException(POSTS_EMPTY_POFOL_ID);
        }

        try{
            List<GetCommentRes> getComment = pofolDao.selectComment(pofolIdx);
            return getComment;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }


    }




}
