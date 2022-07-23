package com.example.demo.src.lesson;

import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class LessonService {

    private final LessonDao lessonDao;
    private final LessonProvider lessonProvider;
    private final JwtService jwtService;

    @Autowired
    public LessonService(LessonDao lessonDao, LessonProvider lessonProvider, JwtService jwtService) {
        this.lessonDao = lessonDao;
        this.lessonProvider = lessonProvider;
        this.jwtService = jwtService;
    }

    // 레슨 생성
    public PostLessonRes createLesson(int userIdx, PostLessonReq postLessonReq) throws BaseException {

        try{

            int lessonIdx = lessonDao.insertLesson(userIdx, postLessonReq);
            //lessonDao.insertMy(userIdx, lessonIdx);
            return new PostLessonRes(lessonIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 레슨 수정
    public void modifyLesson(int lessonIdx, PatchLessonReq patchLessonReq) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) == 0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }

        try{
            int result = lessonDao.updateLesson(lessonIdx, patchLessonReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_LESSON);
            }
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 레슨 삭제
    public void deleteLesson(int lessonIdx) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) ==0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }

        try{
            int result = lessonDao.updateLessonStatus(lessonIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_LESSON);
            }
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 레슨 신청
    public PostSignUpRes applyLesson(int userIdx, int lessonIdx) throws BaseException {

        try{
            int lessonUserIdx = lessonDao.insertSignUp(userIdx, lessonIdx);
            return new PostSignUpRes(lessonUserIdx);
        } catch (Exception exception) {
            //System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }



    // 레슨 좋아요
    public PostLesLikeRes likesLesson(int userIdx, int lessonIdx) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) == 0){
            throw new BaseException(POSTS_EMPTY_LESSON_ID);
        }


        try{
            int result = lessonDao.updateLikes(userIdx, lessonIdx);
            if(result == 0){
                throw new BaseException(LIKES_FAIL_LESSON);
            }
            return new PostLesLikeRes(result);
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }



    // 레슨 좋아요 취소
    public void unlikesLesson(int userIdx, int lessonIdx) throws BaseException {

        if(lessonProvider.checkLessonExist(lessonIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }


        try{
            int result = lessonDao.updateUnlikes(userIdx, lessonIdx);
            if(result == 0){
                throw new BaseException(UNLIKES_FAIL_POFOL);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }



    }



}
