package com.example.demo.src.lesson;


import com.example.demo.src.lesson.model.PatchLessonReq;
import com.example.demo.src.lesson.model.PostLessonReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


@Repository
public class LessonDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    // 레슨 확인
    public int checkLessonExist(int lessonIdx){
        String checkLessonExistQuery = "SELECT exists(SELECT lessonIdx FROM Lesson WHERE lessonIdx = ?)";
        int checkLessonExistParams = lessonIdx;
        return this.jdbcTemplate.queryForObject(checkLessonExistQuery,
                int.class,
                checkLessonExistParams);
    }


    // 레슨 생성
    public int insertLesson(int userIdx, PostLessonReq postLessonReq){
        String insertLessonQuery = "INSERT INTO Lesson(userIdx, lessonTitle, lessonIntroduction, lessonRegion, lessonContent, session, capacity, chatRoomLink, lessonImgUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertLessonParams = new Object[]{ userIdx, postLessonReq.getLessonTitle(), postLessonReq.getLessonIntroduction(),
                postLessonReq.getLessonRegion(), postLessonReq.getLessonContent(), postLessonReq.getSession(),
                postLessonReq.getCapacity(), postLessonReq.getChatRoomLink(), postLessonReq.getLessonImgUrl() };
        this.jdbcTemplate.update(insertLessonQuery, insertLessonParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 레슨 수정
    public int updateLesson(int lessonIdx, PatchLessonReq patchLessonReq){
        String updateLessonQuery = "UPDATE Lesson SET lessonTitle=?, lessonIntroduction=?, lessonRegion=?, lessonContent=?, session=?," +
                "capacity=?, chatRoomLink=?, lessonImgUrl=? WHERE lessonIdx = ?" ;
        Object[] updateLessonParams = new Object[]{ patchLessonReq.getLessonTitle(), patchLessonReq.getLessonIntroduction(),
                patchLessonReq.getLessonRegion(), patchLessonReq.getLessonContent(), patchLessonReq.getSession(),
                patchLessonReq.getCapacity(), patchLessonReq.getChatRoomLink(), patchLessonReq.getLessonImgUrl(), lessonIdx };

        return this.jdbcTemplate.update(updateLessonQuery,updateLessonParams);
    }

    // 레슨 삭제
    public int updateLessonStatus(int lessonIdx){
        String deleteLessonQuery = "UPDATE Lesson SET status = 'INACTIVE' WHERE lessonIdx = ? ";
        Object[] deleteLessonParams = new Object[]{ lessonIdx };

        return this.jdbcTemplate.update(deleteLessonQuery,deleteLessonParams);
    }



}
