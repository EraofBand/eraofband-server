package com.example.demo.src.session;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class SessionProvider {
    private final SessionDao sessionDao;
    private final JwtService jwtService;

    @Autowired
    public SessionProvider(SessionDao sessionDao, JwtService jwtService) {
        this.sessionDao = sessionDao;
        this.jwtService = jwtService;
    }

    // 유저 확인
    public int checkUserExist(int userIdx) throws BaseException {
        try{
            return sessionDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 밴드 확인
    public int checkBandExist(int bandIdx) throws BaseException{
        try{
            return sessionDao.checkBandExist(bandIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
