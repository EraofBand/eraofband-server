package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.src.session.model.PatchBandReq;
import com.example.demo.src.session.model.PostBandReq;
import com.example.demo.src.session.model.PostBandRes;
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

    // 밴드 생성
    public PostBandRes createBand(int userIdx, PostBandReq postBandReq) throws BaseException {


        try{
            int bandIdx = sessionDao.insertBand(userIdx, postBandReq);
            return new PostBandRes(bandIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 밴드 수정
    public void modifyBand(int userIdx, int bandIdx, PatchBandReq patchBandReq) throws BaseException {

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

    // 밴드 삭제
    public void deleteBand(int userIdx,int bandIdx) throws BaseException {

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


}
