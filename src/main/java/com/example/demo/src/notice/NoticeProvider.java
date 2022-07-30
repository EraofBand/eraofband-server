package com.example.demo.src.notice;

import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.LessonDao;
import com.example.demo.src.lesson.model.*;
import com.example.demo.src.notice.model.GetNoticeRes;
import com.example.demo.src.pofol.model.GetPofolRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_EMPTY_USER_ID;

@Service
public class NoticeProvider {

    private final NoticeDao noticeDao;
    private final JwtService jwtService;


    @Autowired
    public NoticeProvider(NoticeDao noticeDao, JwtService jwtService) {
        this.noticeDao = noticeDao;
        this.jwtService = jwtService;

    }

    /**
     * 알림 조회
     */
    public List<GetNoticeRes> getMyNotice(int userIdx) throws BaseException {


        try{
            List<GetNoticeRes> getMyNotice = noticeDao.getMyNotice(userIdx);

            return getMyNotice;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
