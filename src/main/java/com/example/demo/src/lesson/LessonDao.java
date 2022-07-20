package com.example.demo.src.lesson;


import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.model.GetLessonRes;
import com.example.demo.src.lesson.model.GetMemberRes;
import com.example.demo.src.lesson.model.PatchLessonReq;
import com.example.demo.src.lesson.model.PostLessonReq;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;




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
        String insertLessonQuery = "INSERT INTO Lesson(userIdx, lessonTitle, lessonIntroduction, lessonRegion, lessonContent, mySession, capacity, chatRoomLink, lessonImgUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertLessonParams = new Object[]{ userIdx, postLessonReq.getLessonTitle(), postLessonReq.getLessonIntroduction(),
                postLessonReq.getLessonRegion(), postLessonReq.getLessonContent(), postLessonReq.getMySession(),
                postLessonReq.getCapacity(), postLessonReq.getChatRoomLink(), postLessonReq.getLessonImgUrl() };
        this.jdbcTemplate.update(insertLessonQuery, insertLessonParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 레슨 수정
    public int updateLesson(int lessonIdx, PatchLessonReq patchLessonReq){
        String updateLessonQuery = "UPDATE Lesson SET lessonTitle=?, lessonIntroduction=?, lessonRegion=?, lessonContent=?, mySession=?," +
                "capacity=?, chatRoomLink=?, lessonImgUrl=? WHERE lessonIdx = ?" ;
        Object[] updateLessonParams = new Object[]{ patchLessonReq.getLessonTitle(), patchLessonReq.getLessonIntroduction(),
                patchLessonReq.getLessonRegion(), patchLessonReq.getLessonContent(), patchLessonReq.getMySession(),
                patchLessonReq.getCapacity(), patchLessonReq.getChatRoomLink(), patchLessonReq.getLessonImgUrl(), lessonIdx };

        return this.jdbcTemplate.update(updateLessonQuery,updateLessonParams);
    }

    // 레슨 삭제
    public int updateLessonStatus(int lessonIdx){
        String deleteLessonQuery = "UPDATE Lesson SET status = 'INACTIVE' WHERE lessonIdx = ? ";
        Object[] deleteLessonParams = new Object[]{ lessonIdx };

        return this.jdbcTemplate.update(deleteLessonQuery,deleteLessonParams);
    }

    // 레슨 신청
    public int insertSignUp(int userIdx, int lessonIdx){
        String insertApplyQuery = "INSERT INTO LessonUser(userIdx, lessonIdx) VALUES (?, ?)";
        Object[] insertApplyParams = new Object[]{ userIdx, lessonIdx };
        this.jdbcTemplate.update(insertApplyQuery, insertApplyParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 레슨 멤버 목록
    public List<GetMemberRes> getLessonMembers(int lessonIdx){
        String getLessonMemberQuery = "SELECT u.session as mySession, LU.userIdx as userIdx, u.nickName as nickName\n" +
                "FROM LessonUser as LU JOIN User as u on u.userIdx = LU.userIdx\n" +
                "WHERE lessonIdx = ? and u.status = 'ACTIVE'";
        int getLessonMemberParams = lessonIdx;
        return this.jdbcTemplate.query(getLessonMemberQuery,
                (rs, rowNum) -> new GetMemberRes(
                        rs.getInt("mySession"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName")),
                getLessonMemberParams);
    }

    // 레슨 생성 유저 확인
    public int checkLessonMaker(int lessonIdx){
        String selectUserIdxQuery = "SELECT userIdx FROM Lesson WHERE lessonIdx = ?";
        int selectUserIdxParams = lessonIdx;
        return this.jdbcTemplate.queryForObject(selectUserIdxQuery,
                int.class,
                selectUserIdxParams);
    }

    // 레슨 멤버 확인
    public int checkLessonSession(int userIdx, int lessonIdx){
        String checkUserExistQuery = "SELECT exists(SELECT lessonUserIdx FROM LessonUser WHERE userIdx=? and lessonIdx=?)";
        Object[] checkUserExistParams = new Object[]{ userIdx, lessonIdx };
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    public GetLessonRes getLessonMemberByIdx(int lessonIdx, List<GetMemberRes> lessonMembers){



        String getLessonMemberByIdxQuery = "SELECT l.lessonIdx as lessonIdx, l.userIdx as userIdx, u.nickName as nickName,l.lessonTitle as lessonTitle, l.lessonIntroduction as lessonIntroduction,\n" +
                "                l.lessonRegion as lessonRegion, l.lessonContent as lessonContent, l.mySession as mySession,l.chatRoomLink as chatRoomLink, l.lessonImgUrl as lessonImgUrl\n" +
                "                              FROM Lesson as l JOIN User as u on u.userIdx = l.userIdx\n" +
                "                              WHERE l.lessonIdx=? and l.status='ACTIVE'";
        int getLessonMemberByIdxParams = lessonIdx;
        return this.jdbcTemplate.queryForObject(getLessonMemberByIdxQuery,
                (rs, rowNum) -> new GetLessonRes(
                        rs.getInt("lessonIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction"),
                        rs.getString("lessonRegion"),
                        rs.getString("lessonContent"),
                        rs.getInt("mySession"),
                        lessonMembers,
                        rs.getString("chatRoomLink"),
                        rs.getString("lessonImgUrl")),
                getLessonMemberByIdxParams);
    }

    public GetLessonRes getLessonByIdx(int lessonIdx, List<GetMemberRes> lessonMembers){
        String getLessonByIdxQuery = "SELECT l.lessonIdx as lessonIdx, l.userIdx as userIdx, u.nickName as nickName,\n" +
                "       l.lessonTitle as lessonTitle, l.lessonIntroduction as lessonIntroduction,\n" +
                "       l.lessonRegion as lessonRegion, l.lessonContent as lessonContent,l.mySession as mySession,\n" +
                "       l.lessonImgUrl as lessonImgUrl\n" +
                "FROM Lesson as l JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = l.userIdx\n" +
                "WHERE l.lessonIdx=? and l.status='ACTIVE'";
        int getLessonByIdxParams = lessonIdx;
        return this.jdbcTemplate.queryForObject(getLessonByIdxQuery,
                (rs, rowNum) -> new GetLessonRes(
                        rs.getInt("lessonIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction"),
                        rs.getString("lessonRegion"),
                        rs.getString("lessonContent"),
                        rs.getInt("mySession"),
                        lessonMembers,
                        null,
                        rs.getString("lessonImgUrl")),
                getLessonByIdxParams);
    }






}
