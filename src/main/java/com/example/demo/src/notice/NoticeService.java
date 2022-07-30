package com.example.demo.src.notice;
import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.LessonDao;
import com.example.demo.src.lesson.LessonProvider;
import com.example.demo.src.lesson.model.*;
import com.example.demo.src.session.model.GetBandNotiInfoRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class NoticeService {

    private final NoticeDao noticeDao;
    private final NoticeProvider noticeProvider;
    private final JwtService jwtService;

    @Autowired
    public NoticeService(NoticeDao noticeDao, NoticeProvider noticeProvider, JwtService jwtService) {
        this.noticeDao = noticeDao;
        this.noticeProvider = noticeProvider;
        this.jwtService = jwtService;
    }

    /**
     * 알림 INACTIVE
     */
    public void updateNotice(int userIdx) throws BaseException {


        try{
            int result = noticeDao.updateNoticeStatus(userIdx);
            if(result == 0){

                throw new BaseException(DATABASE_ERROR);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
