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


    // 포트폴리오 생성
    public PostPofolRes createPofol(int userIdx, PostPofolReq postPofolReq) throws BaseException {

        try{
            int pofolIdx = pofolDao.insertPofol(userIdx, postPofolReq);
            return new PostPofolRes(pofolIdx);
        } catch (Exception exception) {

            throw new BaseException(DATABASE_ERROR);
        }
    }



    // 포트폴리오 수정
    public void modifyPofol(int userIdx, int pofolIdx, PatchPofolReq patchPofolReq) throws BaseException {

        if(pofolProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(pofolProvider.checkPofolExist(pofolIdx) == 0){
            throw new BaseException(POSTS_EMPTY_POFOL_ID);
        }

        try{
            int result = pofolDao.updatePofol(pofolIdx,patchPofolReq);
//            if(result == 0){
//                throw new BaseException(MODIFY_FAIL_POFOL);
//            }
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }



    // 포트폴리오 삭제
    public void deletePofol(int userIdx,int pofolIdx) throws BaseException {

        if(pofolProvider.checkPofolExist(pofolIdx) ==0){
            throw new BaseException(POSTS_EMPTY_POFOL_ID);
        }

        try{
            int result = pofolDao.updatePofolStatus(pofolIdx);
//            if(result == 0){
//                throw new BaseException(DELETE_FAIL_POFOL);
//            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
