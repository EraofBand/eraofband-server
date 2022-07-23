package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.src.session.model.GetInfoBandRes;
import com.example.demo.src.session.model.GetLikesBandRes;
import com.example.demo.src.session.model.GetFameBandRes;
import com.example.demo.src.session.model.GetNewBandRes;
import com.example.demo.src.session.model.GetSessionRes;
import com.example.demo.src.session.model.GetBandRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class SessionProvider {
    private final SessionDao sessionDao;
    private final JwtService jwtService;
    private List<GetSessionRes> getSessionMembers;
    private List<GetSessionRes> getApplicants;

    @Autowired
    public SessionProvider(SessionDao sessionDao, JwtService jwtService) {
        this.sessionDao = sessionDao;
        this.jwtService = jwtService;
    }

    // 유저 확인
    public int checkBandMaker(int bandIdx) throws BaseException {
        try{
            return sessionDao.checkBandMaker(bandIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 밴드 세션 멤버 확인
    public int checkBandSession(int userIdx, int bandIdx) throws BaseException {
        try {
            return sessionDao.checkBandSession(userIdx, bandIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 밴드 확인
    public int checkBandExist(int bandIdx) throws BaseException {
        try{
            return sessionDao.checkBandExist(bandIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 최신 밴드
    public List<GetNewBandRes> getNewBand() throws BaseException {

        try{
            List<GetNewBandRes> getNewBandRes = sessionDao.getNewBand();
            return getNewBandRes;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 인기 TOP3 밴드
    public List<GetFameBandRes> getFameBand() throws BaseException {

        try{
            List<GetFameBandRes> getFameBandRes = sessionDao.getFameBand();
            return getFameBandRes;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 밴드 조회
    public GetBandRes getBand(int userIdx, int bandIdx) throws BaseException {
        try{
            getSessionMembers = getSessionMembers(bandIdx);
            if (checkBandMaker(bandIdx) == userIdx) {
                getApplicants = getApplicants(bandIdx);
                GetBandRes getBandRes = sessionDao.getMyBandByIdx(bandIdx, getSessionMembers, getApplicants);

                return getBandRes;

            } else if (checkBandSession(userIdx, bandIdx) == 1) {
                GetBandRes getBandRes = sessionDao.getSessionBandByIdx(bandIdx, getSessionMembers);

                return getBandRes;
            } else {
                GetBandRes getBandRes = sessionDao.getBandByIdx(bandIdx, getSessionMembers);

                return getBandRes;
            }
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetSessionRes> getSessionMembers(int bandIdx) throws BaseException {
        try{
            List<GetSessionRes> getSessionMembers = sessionDao.getSessionMembers(bandIdx);
            return getSessionMembers;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetSessionRes> getApplicants(int bandIdx) throws BaseException {
        try{
            List<GetSessionRes> getApplicants = sessionDao.getApplicants(bandIdx);
            return getApplicants;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 찜한 밴드 조회
    public List<GetLikesBandRes> getLikesBand(int userIdx) throws BaseException {

        try{
            List<GetLikesBandRes> getLikesBand = sessionDao.getLikesBand(userIdx);
            return getLikesBand;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetInfoBandRes> getInfoBand(String region, String session) throws BaseException {

        try {
                List<GetInfoBandRes> getInfoBandRes = sessionDao.getInfoBandRes(region, session);
                return getInfoBandRes;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
