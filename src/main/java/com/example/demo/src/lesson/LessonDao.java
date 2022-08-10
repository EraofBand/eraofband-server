package com.example.demo.src.lesson;


import com.example.demo.config.BaseException;
import com.example.demo.src.GetUserTokenRes;
import com.example.demo.src.lesson.model.*;


import com.example.demo.src.session.model.GetBandNotiInfoRes;
import com.example.demo.src.user.model.GetUserLessonRes;
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




    /**
     * 레슨 확인
     * */
    public int checkLessonExist(int lessonIdx){
        String checkLessonExistQuery = "SELECT exists(SELECT lessonIdx FROM Lesson WHERE lessonIdx = ? and status = 'ACTIVE')";
        int checkLessonExistParams = lessonIdx;
        return this.jdbcTemplate.queryForObject(checkLessonExistQuery,
                int.class,
                checkLessonExistParams);
    }

    /**
     * 레슨 생성
     * */
    public int insertLesson(int userIdx, PostLessonReq postLessonReq){
        String insertLessonQuery = "INSERT INTO Lesson(userIdx, lessonTitle, lessonIntroduction, lessonRegion, lessonContent, lessonSession, capacity, chatRoomLink, lessonImgUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertLessonParams = new Object[]{ userIdx, postLessonReq.getLessonTitle(), postLessonReq.getLessonIntroduction(),
                postLessonReq.getLessonRegion(), postLessonReq.getLessonContent(), postLessonReq.getLessonSession(),
                postLessonReq.getCapacity(), postLessonReq.getChatRoomLink(), postLessonReq.getLessonImgUrl() };
        this.jdbcTemplate.update(insertLessonQuery, insertLessonParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     * 레슨 수정
     * */
    public int updateLesson(int lessonIdx, PatchLessonReq patchLessonReq){
        String updateLessonQuery = "UPDATE Lesson SET lessonTitle=?, lessonIntroduction=?, lessonRegion=?, lessonContent=?, lessonSession=?," +
                "capacity=?, chatRoomLink=?, lessonImgUrl=? WHERE lessonIdx = ? and status='ACTIVE'" ;
        Object[] updateLessonParams = new Object[]{ patchLessonReq.getLessonTitle(), patchLessonReq.getLessonIntroduction(),
                patchLessonReq.getLessonRegion(), patchLessonReq.getLessonContent(), patchLessonReq.getLessonSession(),
                patchLessonReq.getCapacity(), patchLessonReq.getChatRoomLink(), patchLessonReq.getLessonImgUrl(), lessonIdx };

        return this.jdbcTemplate.update(updateLessonQuery,updateLessonParams);
    }

    /**
     * 레슨 삭제
     * */
    public int updateLessonStatus(int lessonIdx){
        String deleteLessonQuery = "update Lesson l" +
                "    left join LessonUser as lu on (lu.lessonIdx=l.lessonIdx)\n" +
                "    left join LessonLike as ll on (ll.lessonIdx=l.lessonIdx)\n" +
                "        set l.status='INACTIVE',\n" +
                "            lu.status='INACTIVE',\n" +
                "            ll.status='INACTIVE'\n" +
                "   where l.lessonIdx = ? ";
        Object[] deleteLessonParams = new Object[]{ lessonIdx };

        return this.jdbcTemplate.update(deleteLessonQuery,deleteLessonParams);
    }

    /**
     * 레슨 신청
     * */
    public int insertSignUp(int userIdx, int lessonIdx){
        String insertApplyQuery = "INSERT INTO LessonUser(userIdx, lessonIdx) VALUES (?, ?)";
        Object[] insertApplyParams = new Object[]{ userIdx, lessonIdx };
        this.jdbcTemplate.update(insertApplyQuery, insertApplyParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     * 레슨 지원 유저의 정보 얻기
     */
    public GetLessonNotiInfoRes Noti(int lessonUserIdx) {
        String getLessonInfoQuery = "SELECT lu.lessonUserIdx as lessonUserIdx,\n" +
                "       lu.userIdx as userIdx,\n" +
                "       l.userIdx as receiverIdx,\n" +
                "       l.lessonIdx as lessonIdx,\n" +
                "       u.nickName as nickName,\n" +
                "       u.profileImgUrl as profileImgUrl,\n" +
                "       l.lessonTitle as lessonTitle\n" +
                "FROM LessonUser as lu\n" +
                "join User as u on u.userIdx = lu.userIdx\n" +
                "left join Lesson as l  on lu.lessonIdx = l.lessonIdx\n" +
                "WHERE lu.lessonUserIdx = ? and l.status = 'ACTIVE'\n" +
                "group by lu.lessonUserIdx order by lu.lessonUserIdx";
        int getLessonInfoParams = lessonUserIdx;
        return this.jdbcTemplate.queryForObject(getLessonInfoQuery,
                (rs, rowNum) -> new GetLessonNotiInfoRes(
                        rs.getInt("userIdx"),
                        rs.getInt("receiverIdx"),
                        rs.getInt("lessonIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("lessonTitle")
                ),
                getLessonInfoParams);
    }

    /**
     * 알림 테이블에 추가
     */
    public void LessonNoti(GetLessonNotiInfoRes getLessonNotiInfoRes) {
        String updateLessonNotiQuery = "INSERT INTO Notice(receiverIdx, image, head, body) VALUES (?,?,?,?)";
        Object[] updateLessonNotiParams = new Object[]{getLessonNotiInfoRes.getReceiverIdx(), getLessonNotiInfoRes.getProfileImgUrl(), "레슨 지원",
                getLessonNotiInfoRes.getNickName() + "님이 회원님의 " + getLessonNotiInfoRes.getLessonTitle() + "에 지원하셨습니다."};

        this.jdbcTemplate.update(updateLessonNotiQuery, updateLessonNotiParams);
    }


    /**
     *  레슨 탈퇴
     * */
    public int withdrawLesson(int userIdx, int lessonIdx) {
        String updatewithdrawQuery = "DELETE FROM LessonUser WHERE userIdx = ? and lessonIdx = ?";
        Object[] updatewithdrawParams = new Object[]{userIdx, lessonIdx};

        return this.jdbcTemplate.update(updatewithdrawQuery, updatewithdrawParams);
    }


    /**
     * 레슨 멤버 목록
     * */
    public List<GetMemberRes> getLessonMembers(int lessonIdx){
        String getLessonMemberQuery = "SELECT u.userSession as mySession, LU.userIdx as userIdx, u.nickName as nickName, u.profileImgUrl as profileImgUrl, u.introduction as introduction\n" +
                "                FROM LessonUser as LU JOIN User as u on u.userIdx = LU.userIdx\n" +
                "                LEFT join Lesson as l on l.lessonIdx = LU.lessonIdx\n" +
                "                WHERE LU.lessonIdx = ? and u.status = 'ACTIVE'";
        int getLessonMemberParams = lessonIdx;
        return this.jdbcTemplate.query(getLessonMemberQuery,
                (rs, rowNum) -> new GetMemberRes(
                        rs.getInt("mySession"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("introduction")),
                getLessonMemberParams);
    }

    /**
     * 레슨 생성 유저 확인
     * */
    public int checkLessonMaker(int lessonIdx){
        String selectUserIdxQuery = "SELECT userIdx FROM Lesson WHERE lessonIdx = ? and status='ACTIVE'";
        int selectUserIdxParams = lessonIdx;
        return this.jdbcTemplate.queryForObject(selectUserIdxQuery,
                int.class,
                selectUserIdxParams);
    }


    /**
     * 레슨 멤버 확인
     * */
    public int checkLessonSession(int userIdx, int lessonIdx){
        String checkUserExistQuery = "SELECT exists(SELECT lessonUserIdx FROM LessonUser WHERE userIdx=? and lessonIdx=? and status='ACTIVE')";
        Object[] checkUserExistParams = new Object[]{ userIdx, lessonIdx };
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }



    /**
     *  레슨 소속 유저가 레슨 조회
     * */
    public GetLessonRes getLessonMemberByIdx(int userId, int lessonIdx, List<GetMemberRes> lessonMembers){
        String getLessonMemberByIdxQuery = "\n" +
                "SELECT l.lessonIdx as lessonIdx, l.userIdx as userIdx, u.nickName as nickName,\n" +
                "                   u.profileImgUrl as profileImgUrl, u.introduction as userIntroduction,\n "+
                "                   l.lessonTitle as lessonTitle, l.lessonIntroduction as lessonIntroduction,\n" +
                "                   l.lessonRegion as lessonRegion, l.lessonContent as lessonContent, l.lessonSession as lessonSession,\n" +
                "                   l.chatRoomLink as chatRoomLink, l.lessonImgUrl as lessonImgUrl,\n" +
                "                   IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity,\n" +
                "                   IF(lessonLikeCount is null, 0, lessonLikeCount) as lessonLikeCount,\n" +
                "                   IF(ll.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "                FROM Lesson as l JOIN User as u on u.userIdx = l.userIdx\n" +
                "                   left join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx\n" +
                "                   left join (select lessonIdx, userIdx, count(lessonLikeIdx) as lessonLikeCount from LessonLike WHERE status = 'ACTIVE' group by lessonIdx) plc on plc.lessonIdx = l.lessonIdx\n" +
                "                   left join LessonLike as ll on  ll.lessonIdx = l.lessonIdx and ll.userIdx=?\n" +
                "            WHERE l.lessonIdx=? and l.status='ACTIVE' and u.userIdx = l.userIdx\n" +
                "            GROUP BY l.lessonIdx";

        Object[] getLessonMemberByIdxParams = new Object[]{userId,lessonIdx};
        return this.jdbcTemplate.queryForObject(getLessonMemberByIdxQuery,
                (rs, rowNum) -> new GetLessonRes(
                        rs.getInt("lessonIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userIntroduction"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction"),
                        rs.getString("lessonRegion"),
                        rs.getString("lessonContent"),
                        rs.getInt("lessonSession"),
                        lessonMembers,
                        rs.getString("chatRoomLink"),
                        rs.getString("lessonImgUrl"),
                        rs.getString("likeOrNot"),
                        rs.getInt("lessonLikeCount"),
                        rs.getInt("memberCount"),
                        rs.getInt("capacity")
                ),
                getLessonMemberByIdxParams);
    }



    /**
     *  레슨 미소속 유저가 레슨 조회
     * */
    public GetLessonRes getLessonByIdx(int userId, int lessonIdx, List<GetMemberRes> lessonMembers){
        String getLessonByIdxQuery = "SELECT l.lessonIdx as lessonIdx, l.userIdx as userIdx, u.nickName as nickName,\n" +
                "                       u.profileImgUrl as profileImgUrl, u.introduction as userIntroduction,\n "+
                "                       l.lessonTitle as lessonTitle, l.lessonIntroduction as lessonIntroduction,\n" +
                "                       l.lessonRegion as lessonRegion, l.lessonContent as lessonContent, l.lessonSession as lessonSession,\n" +
                "                       l.lessonImgUrl as lessonImgUrl,\n" +
                "                       IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity,\n" +
                "                       IF(lessonLikeCount is null, 0, lessonLikeCount) as lessonLikeCount,\n" +
                "                       IF(ll.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "                       FROM Lesson as l JOIN User as u on u.userIdx = l.userIdx\n" +
                "                       LEFT join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx\n" +
                "                       LEFT join (select lessonIdx, userIdx, count(lessonLikeIdx) as lessonLikeCount from LessonLike WHERE status = 'ACTIVE' group by lessonIdx) plc on plc.lessonIdx = l.lessonIdx\n" +
                "                       LEFT join LessonLike as ll on ll.lessonIdx = l.lessonIdx and ll.userIdx=?\n" +
                "                       WHERE l.lessonIdx=? and l.status='ACTIVE' and u.userIdx = l.userIdx"+
                "                       GROUP BY l.lessonIdx";


        Object[] getLessonByIdxParams = new Object[]{userId,lessonIdx};
        return this.jdbcTemplate.queryForObject(getLessonByIdxQuery,
                (rs, rowNum) -> new GetLessonRes(
                        rs.getInt("lessonIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userIntroduction"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction"),
                        rs.getString("lessonRegion"),
                        rs.getString("lessonContent"),
                        rs.getInt("lessonSession"),
                        lessonMembers,
                        null,
                        rs.getString("lessonImgUrl"),
                        rs.getString("likeOrNot"),
                        rs.getInt("lessonLikeCount"),
                        rs.getInt("memberCount"),
                        rs.getInt("capacity")
                ),
                getLessonByIdxParams);
    }



    /**
     *  레슨 좋아요
     * */
    public int updateLikes(int userIdx, int lessonIdx) {
        String updateLikesQuery = "INSERT INTO LessonLike(userIdx, lessonIdx) VALUES (?,?)";
        Object[] updateLikesParams = new Object[]{userIdx, lessonIdx};

        this.jdbcTemplate.update(updateLikesQuery, updateLikesParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     *  레슨 좋아요 취소
     * */
    public int updateUnlikes(int userIdx, int lessonIdx) {
        String updateUnlikesQuery = "DELETE FROM LessonLike WHERE userIdx = ? and lessonIdx = ?";
        Object[] updateUnlikesParams = new Object[]{userIdx, lessonIdx};

        return this.jdbcTemplate.update(updateUnlikesQuery, updateUnlikesParams);
    }



    /**
     *  찜한 레슨 조회
     * */
    public List<GetLikesLessonRes> getLikesLesson(int userIdx){
        String getLikesLessonQuery = "\n"+
                "SELECT l.lessonIdx as lessonIdx, l.lessonImgUrl as lessonImgUrl, l.lessonTitle as lessonTitle,"+
        "        l.lessonIntroduction as lessonIntroduction, l.lessonRegion as lessonRegion,"+
        "        IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity"+
        "        FROM LessonUser as lu"+
        "        JOIN Lesson as l"+
        "        left join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx"+
        "        left join LessonLike as ll on l.lessonIdx = ll.lessonIdx"+
        "        WHERE l.status='ACTIVE' and lu.status='ACTIVE' and ll.userIdx=?"+
        "        group by l.lessonIdx"+
        "        order by l.lessonIdx";
        Object[] getLikesLessonParams = new Object[]{userIdx};
        return this.jdbcTemplate.query(getLikesLessonQuery,
                (rs, rowNum) -> new GetLikesLessonRes(
                        rs.getInt("lessonIdx"),
                        rs.getString("lessonImgUrl"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction"),
                        rs.getString("lessonRegion"),
                        rs.getInt("capacity"),
                        rs.getInt("memberCount")),
                getLikesLessonParams);
    }






    /**
     *  지역-세션 분류 레슨 정보 반환
     * */
    public List<GetInfoLessonRes> getInfoLesson(String region, int session){
        region = region.substring(0, 2);

        String getInfoLessonQuery = "";
        Object[] getInfoLessonParams = new Object[]{};
        if (region.compareTo("전체") == 0 && session == 5) {
            getInfoLessonQuery = "\n"+
                    "SELECT l.lessonIdx as lessonIdx, l.lessonImgUrl as lessonImgUrl, l.lessonTitle as lessonTitle,\n" +
                    "                        l.lessonIntroduction as lessonIntroduction, l.lessonRegion as lessonRegion,\n" +
                    "                        IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity\n" +
                    "                        FROM LessonUser as lu\n" +
                    "                        JOIN Lesson as l\n" +
                    "                        left join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx\n" +
                    "                        WHERE l.status='ACTIVE' and lu.status='ACTIVE'\n" +
                    "                        group by l.lessonIdx\n" +
                    "                        order by l.lessonIdx DESC ";
            getInfoLessonParams = new Object[]{};


        } else if (region.compareTo("전체") == 0 && session != 5) {
            getInfoLessonQuery = "\n"+
                    "SELECT l.lessonIdx as lessonIdx, l.lessonImgUrl as lessonImgUrl, l.lessonTitle as lessonTitle,\n" +
                    "                        l.lessonIntroduction as lessonIntroduction, l.lessonRegion as lessonRegion,\n" +
                    "                        IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity\n" +
                    "                        FROM LessonUser as lu\n" +
                    "                        JOIN Lesson as l\n" +
                    "                        left join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx\n" +
                    "                        WHERE l.status='ACTIVE' and lu.status='ACTIVE' and l.lessonSession = ?\n" +
                    "                        group by l.lessonIdx\n" +
                    "                        order by l.lessonIdx DESC ";
            getInfoLessonParams = new Object[]{session};

        } else if (region.compareTo("전체") != 0 && session == 5) {
            getInfoLessonQuery = "\n"+
                    "SELECT l.lessonIdx as lessonIdx, l.lessonImgUrl as lessonImgUrl, l.lessonTitle as lessonTitle,\n" +
                    "                        l.lessonIntroduction as lessonIntroduction, l.lessonRegion as lessonRegion,\n" +
                    "                        IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity\n" +
                    "                        FROM LessonUser as lu\n" +
                    "                        JOIN Lesson as l\n" +
                    "                        left join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx\n" +
                    "                        WHERE l.status='ACTIVE' and lu.status='ACTIVE' and (SUBSTRING(l.lessonRegion, 1, 2)) = ?\n" +
                    "                        group by l.lessonIdx\n" +
                    "                        order by l.lessonIdx DESC";
            getInfoLessonParams = new Object[]{region};

        } else if (region.compareTo("전체") != 0 && session != 5) {

            getInfoLessonQuery = "\n"+
                    "SELECT l.lessonIdx as lessonIdx, l.lessonImgUrl as lessonImgUrl, l.lessonTitle as lessonTitle,\n" +
                    "                        l.lessonIntroduction as lessonIntroduction, l.lessonRegion as lessonRegion,\n" +
                    "                        IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity\n" +
                    "                        FROM LessonUser as lu\n" +
                    "                        JOIN Lesson as l\n" +
                    "                        left join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx\n" +
                    "                        WHERE l.status='ACTIVE' and lu.status='ACTIVE' and (SUBSTRING(l.lessonRegion, 1, 2)) = ? and l.lessonSession = ?\n" +
                    "                        group by l.lessonIdx\n" +
                    "                        order by l.lessonIdx DESC";
            getInfoLessonParams = new Object[]{region, session};

        }

        return this.jdbcTemplate.query(getInfoLessonQuery,
                (rs, rowNum) -> new GetInfoLessonRes(
                        rs.getInt("lessonIdx"),
                        rs.getString("lessonImgUrl"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction"),
                        rs.getString("lessonRegion"),
                        rs.getInt("capacity"),
                        rs.getInt("memberCount")),
                getInfoLessonParams);
    }

    /**
     * FCM 토큰 반환
     */
    public GetUserTokenRes getFCMToken(int userIdx) {
        String getFCMQuery = "select token FROM User WHERE userIdx= ?";
        int getFCMParams = userIdx;

        return this.jdbcTemplate.queryForObject(getFCMQuery,
                (rs, rowNum) -> new GetUserTokenRes(
                        rs.getString("token")),
                getFCMParams);
    }




}
