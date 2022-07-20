package com.example.demo.src.lesson;



import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class LessonProvider {

    private final LessonDao lessonDao;
    private final JwtService jwtService;
    //private List<GetLessonRes> getSessionMembers;
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

}
