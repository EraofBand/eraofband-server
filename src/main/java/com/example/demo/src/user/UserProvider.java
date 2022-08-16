package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    /**
     * 유저 존재 유무 확인
     */
    public int checkUserExist(int userIdx) throws BaseException {
        try{
            return userDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 유저 로그인 상태 확인
     */
    public int checkLogin(int userIdx) throws BaseException {
        try{
            return userDao.checkLogin(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 다른 유저 페이지 조회
     */
    public GetUserFeedRes getUserByIdx(int myId, int userIdx) throws BaseException{
        try{
            GetUserInfoRes getUserInfo=userDao.getUserByIdx(myId, userIdx);
            List<GetUserPofolRes> getUserPofol=userDao.getUserPofol(userIdx);
            List<GetUserBandRes> getUserBand=userDao.getUserBand(userIdx);
            GetUserFeedRes getUserFeed = new GetUserFeedRes(getUserInfo,getUserPofol,getUserBand);
            return getUserFeed;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 마이 페이지 조회
     */
    public GetMyFeedRes getMyFeed(int userIdx) throws BaseException{
        try{
            GetMyInfoRes getMyInfo=userDao.getMyFeed(userIdx);
            List<GetUserPofolRes> getUserPofol=userDao.getUserPofol(userIdx);
            List<GetUserBandRes> getUserBand=userDao.getUserBand(userIdx);
            List<GetUserLessonRes> getUserLesson=userDao.getUserLesson(userIdx);
            GetMyFeedRes getMyFeed = new GetMyFeedRes(getMyInfo,getUserPofol,getUserBand,getUserLesson);
            return getMyFeed;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 팔로잉, 팔로워 리스트 조회
     */
    public GetFollowRes getFollow(int userIdxByJwt, int userIdx) throws BaseException{
        try{
            List<Users> getfollowing=userDao.getFollowing(userIdxByJwt,userIdx);
            List<Users> getfollower=userDao.getFollower(userIdxByJwt ,userIdx);
            GetFollowRes getFollow = new GetFollowRes(getfollowing,getfollower);
            return getFollow;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 이메일 존재 유무 확인
     */
    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
