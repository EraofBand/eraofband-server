package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 다른 유저 페이지 조회
     * */
    public GetUserInfoRes getUserByIdx(int myId,int userIdx){
        String getUsersByIdxQuery = "select u.userIdx as userIdx, u.nickName as nickName,u.gender as gender,u.birth as birth,u.introduction as introduction,u.profileImgUrl as profileImgUrl,u.userSession as userSession,u.region as region," +
                        "IF(pofolCount is null, 0, pofolCount) as pofolCount,IF(followerCount is null, 0, followerCount) as followerCount,IF(followeeCount is null, 0, followeeCount) as followeeCount, follow as follow, u.token as token\n"+
        "from User as u\n"+
            "left join (select userIdx, count(pofolIdx) as pofolCount from Pofol where status='ACTIVE' group by userIdx) p on p.userIdx=u.userIdx\n"+
            "left join (select followerIdx, count(followIdx) as followerCount from Follow where status='ACTIVE' group by followerIdx) fr on fr.followerIdx=u.userIdx\n"+
            "left join (select followeeIdx, count(followIdx) as followeeCount from Follow where status='ACTIVE' group by followeeIdx) fe on fe.followeeIdx=u.userIdx\n"+
            "left join (select exists(select followIdx from Follow where followerIdx=? and followeeIdx=?)as follow) fw on fw.follow\n"+
        "where u.userIdx=? and u.status='ACTIVE'";
        Object[] getUsersByIdxParams = new Object[]{myId,userIdx,userIdx};
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserInfoRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("gender"),
                        rs.getString("birth"),
                        rs.getString("introduction"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("userSession"),
                        rs.getString("region"),
                        rs.getInt("followerCount"),
                        rs.getInt("followeeCount"),
                        rs.getInt("pofolCount"),
                        rs.getInt("follow"),
                        rs.getString("token")),
                getUsersByIdxParams);
    }

    /**
     * 마이 페이지 조회
     * */
    public GetMyInfoRes getMyFeed(int userIdx){
        String getUsersByIdxQuery = "select u.nickName as nickName,u.gender as gender,u.birth as birth,u.introduction as introduction,u.profileImgUrl as profileImgUrl,u.userSession as userSession,u.region as region," +
                "IF(pofolCount is null, 0, pofolCount) as pofolCount,IF(followerCount is null, 0, followerCount) as followerCount,IF(followeeCount is null, 0, followeeCount) as followeeCount\n"+
                "from User as u\n"+
                "left join (select userIdx, count(pofolIdx) as pofolCount from Pofol where status='ACTIVE' group by userIdx) p on p.userIdx=u.userIdx\n"+
                "left join (select followerIdx, count(followIdx) as followerCount from Follow where status='ACTIVE' group by followerIdx) fr on fr.followerIdx=u.userIdx\n"+
                "left join (select followeeIdx, count(followIdx) as followeeCount from Follow where status='ACTIVE' group by followeeIdx) fe on fe.followeeIdx=u.userIdx\n"+
                "where u.userIdx=? and u.status='ACTIVE'";
        int getUsersByIdxParams =userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetMyInfoRes(
                        rs.getString("nickName"),
                        rs.getString("gender"),
                        rs.getString("birth"),
                        rs.getString("introduction"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("userSession"),
                        rs.getString("region"),
                        rs.getInt("followerCount"),
                        rs.getInt("followeeCount"),
                        rs.getInt("pofolCount")),
                getUsersByIdxParams);
    }

    /**
     * 유저 포트폴리오 조회
     * */
    public List<GetUserPofolRes> getUserPofol(int userIdx){
        String getPofolsByIdxQuery = "select p.pofolIdx as pofolIdx, p.imgUrl as imgUrl\n" +
                "from Pofol as p\n" +
                "   join User as u on u.userIdx=p.userIdx\n" +
                "where p.status='ACTIVE' and u.userIdx=?\n" +
                "group by p.pofolIdx\n" +
                "order by p.pofolIdx desc ";
        int getPofolsByIdxParams = userIdx;
        return this.jdbcTemplate.query(getPofolsByIdxQuery,
                (rs, rowNum) -> new GetUserPofolRes(
                        rs.getInt("pofolIdx"),
                        rs.getString("imgUrl")),
                getPofolsByIdxParams);
    }

    /**
     * 유저 소속 밴드 조회
     * */
    public List<GetUserBandRes> getUserBand(int userIdx){
        String getBandsByIdxQuery = "select b.bandIdx as bandIdx, b.bandImgUrl as bandImgUrl, b.bandTitle as bandTitle, b.bandIntroduction as bandIntroduction," +
                "b.bandRegion as bandRegion, IF(memberCount is null, 0, memberCount) as memberCount, b.vocal+b.guitar+b.base+b.keyboard+b.drum as capacity\n" +
                "from BandUser as bu\n" +
                "   left join Band as b on b.bandIdx=bu.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as memberCount from BandUser where status='ACTIVE' group by bandIdx) bm on bm.bandIdx=b.bandIdx\n"+
                "where b.status='ACTIVE' and bu.status='ACTIVE' and (bu.userIdx=? or b.userIdx=?)\n" +
                "group by b.bandIdx\n" +
                "order by b.bandIdx";
        Object[] getBandsByIdxParams = new Object[]{userIdx, userIdx};
        return this.jdbcTemplate.query(getBandsByIdxQuery,
                (rs, rowNum) -> new GetUserBandRes(
                        rs.getInt("bandIdx"),
                        rs.getString("bandImgUrl"),
                        rs.getString("bandTitle"),
                        rs.getString("bandIntroduction"),
                        rs.getString("bandRegion"),
                        rs.getInt("capacity"),
                        rs.getInt("memberCount")),
                getBandsByIdxParams);
    }

    /**
     * 유저 소속 레슨 조회
     * */
    public List<GetUserLessonRes> getUserLesson(int userIdx){
        String getLessonsByIdxQuery = "select l.lessonIdx as lessonIdx, l.lessonImgUrl as lessonImgUrl, l.lessonTitle as lessonTitle, l.lessonIntroduction as lessonIntroduction," +
                "l.lessonRegion as lessonRegion, IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity\n" +
                "from LessonUser as lu\n" +
                "   join Lesson as l on l.lessonIdx=lu.lessonIdx\n" +
                "   left join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx\n"+
                "where l.status='ACTIVE' and lu.status='ACTIVE' and (lu.userIdx=? or l.userIdx=?)\n" +
                "group by l.lessonIdx\n" +
                "order by l.lessonIdx";
        Object[] getLessonsByIdxParams = new Object[]{userIdx, userIdx};
        return this.jdbcTemplate.query(getLessonsByIdxQuery,
                (rs, rowNum) -> new GetUserLessonRes(
                        rs.getInt("lessonIdx"),
                        rs.getString("lessonImgUrl"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction"),
                        rs.getString("lessonRegion"),
                        rs.getInt("capacity"),
                        rs.getInt("memberCount")),
                getLessonsByIdxParams);
    }


    /**
     * 회원가입
     * */
    public int createUser(PostUserReq postUserReq, String email){
        String createUserQuery = "insert into User (nickName,email,birth,gender,profileImgUrl,userSession,region) VALUES (?,?,?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getNickName(),email,postUserReq.getBirth(), postUserReq.getGender(),
                postUserReq.getProfileImgUrl(),postUserReq.getUserSession(),postUserReq.getRegion()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /**
     * 기존 유저 정보 반환
     */
    public User getUserIdx(String email){
        String getIdQuery = "select userIdx, email from User where email=? and status='ACTIVE'";
        String getIdParams = email;
        return this.jdbcTemplate.queryForObject(getIdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("email")),
                getIdParams);
    }

    /**
     * 이메일 존재 유무 확인
     */
    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ? and status='ACTIVE')";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    /**
     * 회원 정보 변경
     */
    public int modifyUserInfo(PatchUserReq patchUserReq){
        String modifyUserInfoQuery = "update User set nickName=?, birth=?, gender=?, introduction=?, profileImgUrl=?, region=? where userIdx = ? and status='ACTIVE'";
        Object[] modifyUserInfoParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getBirth(),
                patchUserReq.getGender(), patchUserReq.getIntroduction(), patchUserReq.getProfileImgUrl(), patchUserReq.getRegion(),patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserInfoQuery,modifyUserInfoParams);
    }

    /**
     * 회원 세션 변경
     */
    public int modifyUserSession(PatchSessionReq patchSessionReq){
        String modifyUserSessionQuery = "update User set userSession =? where userIdx = ? and status='ACTIVE'";
        Object[] modifyUserSessionParams = new Object[]{patchSessionReq.getUserSession(),patchSessionReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserSessionQuery,modifyUserSessionParams);
    }

    /**
     * 회원 삭제
     */
    public int deleteUser(int userIdx){
        String deleteUserQuery = "update User u\n" +
                "    left join Band as b on (b.userIdx=u.userIdx)\n" +
                "    left join BandLike as bl on (bl.userIdx=u.userIdx)\n" +
                "    left join BandUser as bu on (bu.userIdx=u.userIdx)\n" +
                "    left join Follow as f on (f.followerIdx=u.userIdx or f.followeeIdx=u.userIdx)\n" +
                "    left join Lesson as l on (l.userIdx=u.userIdx)\n" +
                "    left join LessonLike as ll on (ll.userIdx=u.userIdx)\n" +
                "    left join LessonUser as lu on (lu.userIdx=u.userIdx)\n" +
                "    left join Pofol as p on (p.userIdx=u.userIdx)\n" +
                "    left join PofolComment as pc on (pc.userIdx=u.userIdx)\n" +
                "    left join PofolLike as pl on (pl.userIdx=u.userIdx)\n" +
                "        set u.status='INACTIVE',\n" +
                "            b.status='INACTIVE',\n" +
                "            bl.status='INACTIVE',\n" +
                "            bu.status='INACTIVE',\n" +
                "            f.status='INACTIVE',\n" +
                "            l.status='INACTIVE',\n" +
                "            ll.status='INACTIVE',\n" +
                "            lu.status='INACTIVE',\n" +
                "            p.status='INACTIVE',\n" +
                "            pc.status='INACTIVE',\n" +
                "            pl.status='INACTIVE'\n" +
                "    where u.userIdx = ?";
        Object[] deleteUserParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }

    /**
     * 팔로우 하기
     */
    public int updateFollow(int myIdx, int userIdx) {
        String updateFollowQuery = "INSERT INTO Follow(followerIdx, followeeIdx) VALUES (?,?)";
        Object[] updateFollowParams = new Object[]{myIdx, userIdx};

        this.jdbcTemplate.update(updateFollowQuery, updateFollowParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     * 팔로우 요청자의 정보 얻기
     */
    public GetUserNotiInfoRes Noti(int myIdx){
        String getInfoQuery = "select userIdx, nickName, profileImgUrl from User where userIdx=? and status='ACTIVE'";
        int getInfoParams = myIdx;
        return this.jdbcTemplate.queryForObject(getInfoQuery,
                (rs, rowNum) -> new GetUserNotiInfoRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl")),
                getInfoParams);
    }

    /**
     * 알림 테이블에 추가
     */
    public void followNoti(GetUserNotiInfoRes getUserNotiInfoRes, int userIdx){
        String updateFollowNotiQuery = "INSERT INTO Notice(receiverIdx, image, head, body) VALUES (?,?,?,?)";
        Object[] updateFollowNotiParams = new Object[]{userIdx, getUserNotiInfoRes.getProfileImgUrl(),"팔로우",
        getUserNotiInfoRes.getNickName()+"님이 회원님을 팔로우 했습니다."};

        this.jdbcTemplate.update(updateFollowNotiQuery, updateFollowNotiParams);
    }

    /**
     * 팔로우 취소하기
     */
    public int updateUnFollow(int myIdx, int userIdx) {
        String updateUnFollowQuery = "DELETE FROM Follow WHERE followerIdx = ? and followeeIdx = ?";
        Object[] updateUnFollowParams = new Object[]{myIdx,userIdx};

        return this.jdbcTemplate.update(updateUnFollowQuery, updateUnFollowParams);
    }

    /**
     * 팔로잉 리스트 조회
     */
    public List<Users> getFollowing(int userIdx){
        String getFollowQuery = "select u.userIdx as userIdx, u.nickName as nickName, u.profileImgUrl as profileImgUrl, u.token as token,\n" +
                "       (IF(exists(select followIdx from Follow where followerIdx = ? and followeeIdx = userIdx), 1, 0)) as follow\n" +
                "                from Follow as f\n" +
                "                    left join User as u on u.userIdx=f.followeeIdx\n" +
                "                where u.status='ACTIVE' and f.status='ACTIVE' and f.followerIdx=? and  u.userIdx=f.followeeIdx\n" +
                "                group by u.userIdx\n" +
                "                order by u.userIdx";
        Object[] getFollowParams = new Object[]{userIdx, userIdx};
        return this.jdbcTemplate.query(getFollowQuery,
                (rs, rowNum) -> new Users(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("token"),
                        rs.getInt("follow")
                ),
                getFollowParams);
    }

    /**
     * 팔로워 리스트 조회
     */
    public List<Users> getFollower(int userIdx){
        String getFollowQuery = "select u.userIdx as userIdx, u.nickName as nickName, u.profileImgUrl as profileImgUrl, u.token as token,\n" +
                "       (IF(exists(select followIdx from Follow where followerIdx = ? and followeeIdx = userIdx), 1, 0)) as follow\n" +
                "                from Follow as f\n" +
                "                    left join User as u on u.userIdx=f.followerIdx\n" +
                "                where u.status='ACTIVE' and f.status='ACTIVE' and f.followeeIdx=?\n" +
                "                group by u.userIdx\n" +
                "                order by u.userIdx";
        Object[] getFollowParams = new Object[]{userIdx, userIdx};
        return this.jdbcTemplate.query(getFollowQuery,
                (rs, rowNum) -> new Users(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("token"),
                        rs.getInt("follow")
                ),
                getFollowParams);
    }
}