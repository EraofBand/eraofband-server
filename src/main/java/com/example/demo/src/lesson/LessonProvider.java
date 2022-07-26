package com.example.demo.src.lesson;



import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.POSTS_EMPTY_LESSON_ID;

@Service
public class LessonProvider {

    private final LessonDao lessonDao;
    private final JwtService jwtService;
    private List<GetMemberRes> getLessonMembers;
    //private List<GetLessonRes> getApplicants;

    @Autowired
    public LessonProvider(LessonDao lessonDao, JwtService jwtService) {
        this.lessonDao = lessonDao;
        this.jwtService = jwtService;

    }


    /**
     *  레슨 확인
     * */
    public int checkLessonExist(int lessonIdx) throws BaseException {
        try {
            return lessonDao.checkLessonExist(lessonIdx);
        } catch (Exception exception) {
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }
    }


    /**
     *  레슨 생성 유저 확인
     * */
    public int checkLessonMaker(int lessonIdx) throws BaseException {
        try {
            return lessonDao.checkLessonMaker(lessonIdx);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     *  레슨 멤버 확인
     * */
    public int checkLessonSession(int userIdx, int lessonIdx) throws BaseException {
        try {
            return lessonDao.checkLessonSession(userIdx, lessonIdx);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     *  레슨 정보 (멤버/멤버 아닌 유저)에 따라 다름
     * */
    public GetLessonRes getLesson(int userIdx, int lessonIdx) throws BaseException {
        try {
            getLessonMembers = getLessonMembers(lessonIdx);
            if (checkLessonSession(userIdx, lessonIdx) == 1 || checkLessonMaker(lessonIdx) == userIdx) {
                GetLessonRes getLessonRes = lessonDao.getLessonMemberByIdx(lessonIdx, getLessonMembers);

                return getLessonRes;
            } else {
                GetLessonRes getLessonRes = lessonDao.getLessonByIdx(lessonIdx, getLessonMembers);

                return getLessonRes;
            }
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     *  레슨 멤버 목록
     * */
    public List<GetMemberRes> getLessonMembers(int lessonIdx) throws BaseException {
        try {
            List<GetMemberRes> getMembers = lessonDao.getLessonMembers(lessonIdx);
            return getMembers;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     *  찜한 레슨 조회
     * */
    public List<GetLikesLessonRes> getLikesLesson(int userIdx) throws BaseException {

        try {
            List<GetLikesLessonRes> getLikesLesson = lessonDao.getLikesLesson(userIdx);
            return getLikesLesson;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }



    /**
     *  지역-세션 분류 레슨 정보 반환
     * */
    public List<GetInfoLessonRes> getInfoLesson(String region, int session) throws BaseException {

        try {
            List<GetInfoLessonRes> getInfoLesson = lessonDao.getInfoLesson(region, session);
            return getInfoLesson;


        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }





}

