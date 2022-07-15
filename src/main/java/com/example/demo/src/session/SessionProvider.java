package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.src.session.model.GetApplyRes;
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
    private List<GetApplyRes> getApplicants;

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

    // 밴드 확인
    public int checkBandExist(int bandIdx) throws BaseException {
        try{
            return sessionDao.checkBandExist(bandIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 밴드 조회
    public GetBandRes getBand(int userIdx, int bandIdx) throws BaseException {
        try{
            if (checkBandMaker(bandIdx) == userIdx) {

                try {
                    getApplicants = getApplicants(bandIdx);
                } catch(Exception exception){
//                    System.out.println(exception);

                    throw new BaseException(DATABASE_ERROR);
                }
                GetBandRes getBandRes = sessionDao.getMyBandByIdx(bandIdx, getApplicants);
                return getBandRes;
            }
            else {
                GetBandRes getBandRes = sessionDao.getBandByIdx(bandIdx);
                return getBandRes;
            }
        } catch(Exception exception){
//            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetApplyRes> getApplicants(int bandIdx) throws BaseException {
        try{
            List<GetApplyRes> getApplicants = sessionDao.getApplicants(bandIdx);
            return getApplicants;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
