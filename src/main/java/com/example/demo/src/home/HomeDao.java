package com.example.demo.src.home;

import com.example.demo.config.BaseException;
import com.example.demo.src.home.model.GetFameLessonRes;
import com.example.demo.src.lesson.model.*;


import com.example.demo.src.user.model.GetUserLessonRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;




@Repository
public class HomeDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // 인기 TOP3 레슨
    public List<GetFameLessonRes> getFameLesson(){
        String getFameLessonQuery = "\n"+
                "SELECT l.lessonIdx as lessonIdx, l.lessonImgUrl as lessonImgUrl, l.lessonTitle as lessonTitle,\n" +
                "                      l.lessonIntroduction as lessonIntroduction\n" +
                "                    FROM Lesson as l\n" +
                "                    left join (select lessonIdx, userIdx, count(lessonLikeIdx) as lessonLikeCount from LessonLike WHERE status = 'ACTIVE' group by lessonIdx) plc on plc.lessonIdx = l.lessonIdx\n" +
                "                      WHERE l.status='ACTIVE'\n" +
                "                   order by lessonLikeCount DESC\n" +
                "LIMIT 3";
        Object[] getFameLessonParams = new Object[]{};
        return this.jdbcTemplate.query(getFameLessonQuery,
                (rs, rowNum) -> new GetFameLessonRes(
                        rs.getInt("lessonIdx"),
                        rs.getString("lessonImgUrl"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction")
                        ),
                getFameLessonParams);
    }



}
