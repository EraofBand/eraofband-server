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
        String checkLessonExistQuery = "SELECT exists(SELECT lessonIdx FROM Lesson WHERE lessonIdx = ? and status = 'ACTIVE')";
        int checkLessonExistParams = lessonIdx;
        return this.jdbcTemplate.queryForObject(checkLessonExistQuery,
                int.class,
                checkLessonExistParams);
    }




    // 레슨 생성
    public int insertLesson(int userIdx, PostLessonReq postLessonReq){
        String insertLessonQuery = "INSERT INTO Lesson(userIdx, lessonTitle, lessonIntroduction, lessonRegion, lessonContent, lessonSession, capacity, chatRoomLink, lessonImgUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertLessonParams = new Object[]{ userIdx, postLessonReq.getLessonTitle(), postLessonReq.getLessonIntroduction(),
                postLessonReq.getLessonRegion(), postLessonReq.getLessonContent(), postLessonReq.getLessonSession(),
                postLessonReq.getCapacity(), postLessonReq.getChatRoomLink(), postLessonReq.getLessonImgUrl() };
        this.jdbcTemplate.update(insertLessonQuery, insertLessonParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 레슨 수정
    public int updateLesson(int lessonIdx, PatchLessonReq patchLessonReq){
        String updateLessonQuery = "UPDATE Lesson SET lessonTitle=?, lessonIntroduction=?, lessonRegion=?, lessonContent=?, lessonSession=?," +
                "capacity=?, chatRoomLink=?, lessonImgUrl=? WHERE lessonIdx = ?" ;
        Object[] updateLessonParams = new Object[]{ patchLessonReq.getLessonTitle(), patchLessonReq.getLessonIntroduction(),
                patchLessonReq.getLessonRegion(), patchLessonReq.getLessonContent(), patchLessonReq.getLessonSession(),
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
        String getLessonMemberQuery = "SELECT u.usersession as mySession, LU.userIdx as userIdx, u.nickName as nickName\n" +
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


    // 레슨 소속 유저가 레슨 조회
    public GetLessonRes getLessonMemberByIdx(int lessonIdx, List<GetMemberRes> lessonMembers){
        String getLessonMemberByIdxQuery = "\n" +
                "SELECT l.lessonIdx as lessonIdx, l.userIdx as userIdx, u.nickName as nickName,\n" +
                "   l.lessonTitle as lessonTitle, l.lessonIntroduction as lessonIntroduction,\n" +
                "   l.lessonRegion as lessonRegion, l.lessonContent as lessonContent, l.lessonSession as lessonSession, \n" +
                "   l.chatRoomLink as chatRoomLink, l.lessonImgUrl as lessonImgUrl,\n" +
                "   IF(lessonLikeCount is null, 0, lessonLikeCount) as lessonLikeCount,"+
                "   IF(ll.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n"+
                "FROM Lesson as l JOIN User as u on u.userIdx = l.userIdx\n" +
                "left join (select lessonIdx, userIdx, count(lessonLikeIdx) as lessonLikeCount from LessonLike WHERE status = 'ACTIVE' group by lessonIdx) plc on plc.lessonIdx = l.lessonIdx\n"+
                "left join LessonLike as ll on  ll.lessonIdx = l.lessonIdx\n" +
                "WHERE l.lessonIdx=? and l.status='ACTIVE' and u.userIdx = l.userIdx";



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
                        rs.getInt("lessonSession"),
                        lessonMembers,
                        rs.getString("chatRoomLink"),
                        rs.getString("lessonImgUrl"),
                        rs.getString("likeOrNot"),
                        rs.getInt("lessonLikeCount")),
                getLessonMemberByIdxParams);
    }



    // 레슨 미소속 유저가 레슨 조회
    public GetLessonRes getLessonByIdx(int lessonIdx, List<GetMemberRes> lessonMembers){
        String getLessonByIdxQuery = "SELECT l.lessonIdx as lessonIdx, l.userIdx as userIdx, u.nickName as nickName,\n" +
                "       l.lessonTitle as lessonTitle, l.lessonIntroduction as lessonIntroduction,\n" +
                "       l.lessonRegion as lessonRegion, l.lessonContent as lessonContent, l.lessonSession as lessonSession,\n" +
                "       l.lessonImgUrl as lessonImgUrl,\n" +
                "       IF(lessonLikeCount is null, 0, lessonLikeCount) as lessonLikeCount,"+
                "       IF(ll.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n"+
                "       FROM Lesson as l JOIN User as u on u.userIdx = l.userIdx\n" +
                "       left join (select lessonIdx, userIdx, count(lessonLikeIdx) as lessonLikeCount from LessonLike WHERE status = 'ACTIVE' group by lessonIdx) plc on plc.lessonIdx = l.lessonIdx\n"+
                "       left join LessonLike as ll on  ll.lessonIdx = l.lessonIdx\n" +
                "WHERE l.lessonIdx=? and l.status='ACTIVE' and u.userIdx = l.userIdx";


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
                        rs.getInt("lessonSession"),
                        lessonMembers,
                        null,
                        rs.getString("lessonImgUrl"),
                        rs.getString("likeOrNot"),
                        rs.getInt("lessonLikeCount")),
                getLessonByIdxParams);
    }

    // 레슨 좋아요
    public int updateLikes(int userIdx, int lessonIdx) {
        String updateLikesQuery = "INSERT INTO LessonLike(userIdx, lessonIdx) VALUES (?,?)";
        Object[] updateLikesParams = new Object[]{userIdx, lessonIdx};

        this.jdbcTemplate.update(updateLikesQuery, updateLikesParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 레슨 좋아요 취소
    public int updateUnlikes(int userIdx, int lessonIdx) {
        String updateUnlikesQuery = "DELETE FROM LessonLike WHERE userIdx = ? and lessonIdx = ?";
        Object[] updateUnlikesParams = new Object[]{userIdx, lessonIdx};

        return this.jdbcTemplate.update(updateUnlikesQuery, updateUnlikesParams);
    }






}
