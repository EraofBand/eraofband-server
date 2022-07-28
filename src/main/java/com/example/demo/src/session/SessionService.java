package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.model.PostLesLikeRes;
import com.example.demo.src.pofol.model.GetComNotiInfoRes;
import com.example.demo.src.session.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service // Service Create 로직 처리
public class SessionService {
    private final SessionDao sessionDao;
    private final SessionProvider sessionProvider;
    private final JwtService jwtService;

    @Autowired
    public SessionService(SessionDao sessionDao, SessionProvider sessionProvider, JwtService jwtService) {
        this.sessionDao = sessionDao;
        this.sessionProvider = sessionProvider;
        this.jwtService = jwtService;
    }

    /**
     * 밴드 생성
     * */
    public PostBandRes createBand(int userIdx, PostBandReq postBandReq) throws BaseException {

        try{
            int bandIdx = sessionDao.insertBand(userIdx, postBandReq);
            return new PostBandRes(bandIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 수정
     * */
    public void modifyBand(int bandIdx, PatchBandReq patchBandReq) throws BaseException {

        if(sessionProvider.checkBandExist(bandIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BAND_ID);
        }

        try{
            int result = sessionDao.updateBand(bandIdx, patchBandReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_BAND);
            }
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 삭제
     * */
    public void deleteBand(int bandIdx) throws BaseException {

        if(sessionProvider.checkBandExist(bandIdx) ==0){
            throw new BaseException(POSTS_EMPTY_BAND_ID);
        }

        try{
            int result = sessionDao.updateBandStatus(bandIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_BAND);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 밴드 지원
     * */
    public PostApplyRes applySession(int userIdx, int bandIdx, PostApplyReq postApplyReq) throws BaseException {

        try{
            int bandUserIdx = sessionDao.insertApply(userIdx, bandIdx, postApplyReq);

            //밴드 지원 유저의 정보 얻기
            GetBandNotiInfoRes getBandNotiInfoRes=sessionDao.Noti(bandUserIdx);
            //알림 테이블에 추가
            sessionDao.BandNoti(getBandNotiInfoRes);
            return new PostApplyRes(bandUserIdx);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 지원 수락
     * */
    public void acceptSession(int bandIdx, int userIdx) throws BaseException {

        if(sessionProvider.checkBandExist(bandIdx) ==0){
            throw new BaseException(POSTS_EMPTY_BAND_ID);
        }

        try{
            sessionDao.acceptSession(bandIdx, userIdx);

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 지원 거절
     * */
    public void rejectSession(int bandIdx, int userIdx) throws BaseException {

        if(sessionProvider.checkBandExist(bandIdx) ==0){
            throw new BaseException(POSTS_EMPTY_BAND_ID);
        }

        try{
            sessionDao.rejectSession(bandIdx, userIdx);

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 좋아요
     * */
    public PostBandLikeRes likesBand(int userIdx, int bandIdx) throws BaseException {

        if(sessionProvider.checkBandExist(bandIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BAND_ID);
        }

        try{
            int result = sessionDao.updateLikes(userIdx, bandIdx);
            if(result == 0){
                throw new BaseException(LIKES_FAIL_BAND);
            }
            return new PostBandLikeRes(result);
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     * 밴드 좋아요 취소
     * */
    public void unlikesBand(int userIdx, int bandIdx) throws BaseException {

        if(sessionProvider.checkBandExist(bandIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BAND_ID);
        }

        try{
            int result = sessionDao.updateUnlikes(userIdx, bandIdx);
            if(result == 0){
                throw new BaseException(UNLIKES_FAIL_BAND);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
