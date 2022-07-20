package com.example.demo.src.lesson;



import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.model.GetLessonRes;
import com.example.demo.src.lesson.model.GetMemberRes;
import com.example.demo.src.session.model.GetBandRes;
import com.example.demo.src.session.model.GetSessionRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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

    // 레슨 확인
    public int checkLessonExist(int lessonIdx) throws BaseException {
        try{
            return lessonDao.checkLessonExist(lessonIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkLessonMaker(int lessonIdx) throws BaseException {
        try{
            return lessonDao.checkLessonMaker(lessonIdx);
        } catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 레슨 멤버 확인
    public int checkLessonSession(int userIdx, int lessonIdx) throws BaseException {
        try {
            return lessonDao.checkLessonSession(userIdx, lessonIdx);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetLessonRes getLesson(int userIdx, int lessonIdx) throws BaseException {
        try{
            getLessonMembers = getLessonMembers(lessonIdx);
            if (checkLessonSession(userIdx, lessonIdx) == 1 || checkLessonMaker(lessonIdx) == userIdx) {
                GetLessonRes getLessonRes = lessonDao.getLessonMemberByIdx(lessonIdx, getLessonMembers);

                return getLessonRes;
            } else {
                GetLessonRes getLessonRes = lessonDao.getLessonByIdx(lessonIdx, getLessonMembers);

                return getLessonRes;
            }
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 레슨 멤버 목록
    public List<GetMemberRes> getLessonMembers(int lessonIdx) throws BaseException {
        try{
            List<GetMemberRes> getMembers = lessonDao.getLessonMembers(lessonIdx);
            return getMembers;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
