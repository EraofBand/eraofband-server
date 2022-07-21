package com.example.demo.src.home;
import com.example.demo.config.BaseException;
import com.example.demo.src.home.model.GetFameLessonRes;
import com.example.demo.src.lesson.LessonDao;
import com.example.demo.src.lesson.model.GetLessonRes;
import com.example.demo.src.lesson.model.GetLikesLessonRes;
import com.example.demo.src.lesson.model.GetMemberRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.POSTS_EMPTY_LESSON_ID;

@Service
public class HomeProvider {

    private final HomeDao homeDao;
    private final JwtService jwtService;

    @Autowired
    public HomeProvider(HomeDao homeDao, JwtService jwtService) {
        this.homeDao = homeDao;
        this.jwtService = jwtService;

    }

    // 인기 TOP3 레슨
    public List<GetFameLessonRes> getFameLesson() throws BaseException {

        try{
            List<GetFameLessonRes> getFameLessonRes = homeDao.getFameLesson();
            return getFameLessonRes;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
