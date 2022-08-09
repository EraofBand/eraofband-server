package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.src.pofol.model.GetPofolRes;
import com.example.demo.src.session.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class SessionProvider {
    private final SessionDao sessionDao;
    private final JwtService jwtService;
    private List<GetSessionMemRes> getSessionMembers;
    private List<GetSessionAppRes> getApplicants;

    @Autowired
    public SessionProvider(SessionDao sessionDao, JwtService jwtService) {
        this.sessionDao = sessionDao;
        this.jwtService = jwtService;
    }

    /**
     * 밴드 생성 유저 확인
     * */
    public int checkBandMaker(int bandIdx) throws BaseException {
        try{
            return sessionDao.checkBandMaker(bandIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 멤버 확인
     * */
    public int checkBandSession(int userIdx, int bandIdx) throws BaseException {
        try {
            return sessionDao.checkBandSession(userIdx, bandIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 지원자 확인
     * */
    public int checkBandApply(int userIdx, int bandIdx) throws BaseException {
        try {
            return sessionDao.checkBandApply(userIdx, bandIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 존재 유무 확인
     * */
    public int checkBandExist(int bandIdx) throws BaseException {
        try{
            return sessionDao.checkBandExist(bandIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 최신 밴드 조회
     * */
    public List<GetNewBandRes> getNewBand() throws BaseException {

        try{
            List<GetNewBandRes> getNewBandRes = sessionDao.getNewBand();
            return getNewBandRes;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 인기 밴드 top3 조회
     * */
    public List<GetFameBandRes> getFameBand() throws BaseException {

        try{
            List<GetFameBandRes> getFameBandRes = sessionDao.getFameBand();
            return getFameBandRes;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 정보 조회
     * */
    public GetBandRes getBand(int userIdx, int bandIdx) throws BaseException {
        try{
            getSessionMembers = getSessionMembers(bandIdx);
            //밴드 생성자일 경우
            if (checkBandMaker(bandIdx) == userIdx) {
                getApplicants = getApplicants(bandIdx);
                GetBandRes getBandRes = sessionDao.getMyBandByIdx(userIdx,bandIdx, getSessionMembers, getApplicants);

                return getBandRes;
            }
            //밴드 멤버일 경우
            else if (checkBandSession(userIdx, bandIdx) == 1) {
                GetBandRes getBandRes = sessionDao.getSessionBandByIdx(userIdx,bandIdx, getSessionMembers);

                return getBandRes;
            }
            //밴드 외부 유저일 경우
            else {
                GetBandRes getBandRes = sessionDao.getBandByIdx(userIdx, bandIdx, getSessionMembers);

                return getBandRes;
            }
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 멤버 조회
     * */
    public List<GetSessionMemRes> getSessionMembers(int bandIdx) throws BaseException {
        try{
            List<GetSessionMemRes> getSessionMembers = sessionDao.getSessionMembers(bandIdx);
            return getSessionMembers;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 지원자 조회
     * */
    public List<GetSessionAppRes> getApplicants(int bandIdx) throws BaseException {
        try{
            List<GetSessionAppRes> getApplicants = sessionDao.getApplicants(bandIdx);
            return getApplicants;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 찜한 밴드 조회
     * */
    public List<GetLikesBandRes> getLikesBand(int userIdx) throws BaseException {

        try{
            List<GetLikesBandRes> getLikesBand = sessionDao.getLikesBand(userIdx);
            return getLikesBand;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 지역-세션 분류 밴드 검색 조회
     * */
    public List<GetInfoBandRes> getInfoBand(String region, int session) throws BaseException {

        try {
                List<GetInfoBandRes> getInfoBandRes = sessionDao.getInfoBandRes(region, session);
                return getInfoBandRes;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 밴드 앨범 존재 유무 확인
     * */
    public int checkAlbumExist(int albumIdx) throws BaseException {
        try{
            return sessionDao.checkAlbumExist(albumIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 밴드 앨범 리스트 조회
     */
    public List<GetAlbumRes> getBandAlbum(int bandIdx) throws BaseException {

        if(checkBandExist(bandIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BAND_ID);
        }

        try{
            List<GetAlbumRes> getBandAlbum = sessionDao.selectBandAlbum(bandIdx);
            return getBandAlbum;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
