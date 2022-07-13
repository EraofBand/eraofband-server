package com.example.demo.src.pofol;


import com.example.demo.src.pofol.model.*;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PofolDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // 유저 확인
    public int checkUserExist(int userIdx) {
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ? and status = 'ACTIVE')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    // 포트폴리오 확인
    public int checkPofolExist(int pofolIdx) {
        String checkPostExistQuery = "select exists(select pofolIdx from Pofol where pofolIdx = ? and status = 'ACTIVE')";
        int checkPostExistParams = pofolIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    // 포트폴리오 댓글 확인
    public int checkCommentExist(int pofolCommentIdx) {
        String checkCommentExistQuery = "select exists(select pofolCommentIdx from PofolComment where pofolCommentIdx = ? and status = 'ACTIVE')";
        int checkCommentExistParams = pofolCommentIdx;
        return this.jdbcTemplate.queryForObject(checkCommentExistQuery,
                int.class,
                checkCommentExistParams);

    }

    // 이메일 확인
    public int checkEmailExist(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    // 팔로우 한 유저 포트폴리오 리스트 조회

    public List<GetPofolRes> selectPofol(int userIdx) {
        String selectUserPofolQuery = "\n" +
                "        SELECT p.PofolIdx as PofolIdx,\n" +
                "            u.userIdx as userIdx,\n" +
                "            u.nickName as nickName,\n" +
                "            u.profileImgUrl as profileImgUrl,\n" +
                "            p.title as title,\n" +
                "            p.content as content,\n" +
                "            p.videoUrl as videoUrl,\n" +
                "            IF(pofolLikeCount is null, 0, pofolLikeCount) as pofolLikeCount,\n" +
                "            IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                "            case\n" +
                "                when timestampdiff(second, p.updatedAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')\n" +
                "                when timestampdiff(minute , p.updatedAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전')\n" +
                "                when timestampdiff(hour , p.updatedAt, current_timestamp) < 24\n" +
                "                    then concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')\n" +
                "                when timestampdiff(day , p.updatedAt, current_timestamp) < 365\n" +
                "                    then concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전')\n" +
                "                else timestampdiff(year , p.updatedAt, current_timestamp)\n" +
                "            end as updatedAt,\n" +
                "            IF(pl.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "        FROM Pofol as p\n" +
                "            join User as u on u.userIdx = p.userIdx\n" +
                "            left join (select pofolIdx, userIdx, count(pofolLikeIdx) as pofolLikeCount from PofolLike WHERE status = 'ACTIVE' group by pofolIdx) plc on plc.pofolIdx = p.pofolIdx\n" +
                "            left join (select pofolIdx, count(pofolCommentIdx) as commentCount from PofolComment WHERE status = 'ACTIVE' group by pofolIdx) c on c.pofolIdx = p.pofolIdx\n" +
                "            left join Follow as f on f.followeeIdx = p.userIdx and f.status = 'ACTIVE'\n" +
                "            left join PofolLike as pl on pl.userIdx = f.followerIdx and pl.pofolIdx = p.pofolIdx\n" +
                "        WHERE f.followerIdx = ? and p.status = 'ACTIVE'\n" +
                "        group by p.pofolIdx;\n";
        int selectUserPofolParam = userIdx;
        return this.jdbcTemplate.query(selectUserPofolQuery,
                (rs, rowNum) -> new GetPofolRes(
                        rs.getInt("pofolIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("pofolLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                        rs.getString("videoUrl")
                ), selectUserPofolParam);


    }


    // 회원 확인
    public String checkUserStatus(String email) {
        String checkUserStatusQuery = "select status from User where email = ? ";
        String checkUserStatusParams = email;
        return this.jdbcTemplate.queryForObject(checkUserStatusQuery,
                String.class,
                checkUserStatusParams);

    }

    // 포트폴리오, 유저 확인
    public int checkUserPofolExist(int userIdx, int pofolIdx) {
        String checkUserPofolQuery = "select exists(select pofolIdx from Pofol where pofolIdx = ? and userIdx=?) ";
        Object[] checkUserPofolParams = new Object[]{pofolIdx, userIdx};
        return this.jdbcTemplate.queryForObject(checkUserPofolQuery,
                int.class,
                checkUserPofolParams);

    }

    // 포트폴리오 생성
    public int insertPofol(int userIdx, PostPofolReq postPofolReq) {
        String insertPofolQuery = "INSERT INTO Pofol(userIdx, content, videoUrl, title, imgUrl) VALUES (?, ?, ?, ?, ?)";
        Object[] insertPofolParams = new Object[]{userIdx, postPofolReq.getContent(), postPofolReq.getVideoUrl(), postPofolReq.getTitle(), postPofolReq.getImgUrl()};
        this.jdbcTemplate.update(insertPofolQuery, insertPofolParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }


    // 포트폴리오 수정
    public int updatePofol(int pofolIdx, PatchPofolReq patchPofolReq) {
        String updatePofolQuery = "UPDATE Pofol SET title = ?, content = ? WHERE pofolIdx = ?\n";
        Object[] updatePofolParams = new Object[]{patchPofolReq.getTitle(), patchPofolReq.getContent(), pofolIdx};

        return this.jdbcTemplate.update(updatePofolQuery, updatePofolParams);
    }

    // 포트폴리오 삭제
    public int updatePofolStatus(int pofolIdx) {
        String deleteUserQuery = "UPDATE Pofol SET status = 'INACTIVE' WHERE pofolIdx = ? ";
        Object[] deleteUserParams = new Object[]{pofolIdx};

        return this.jdbcTemplate.update(deleteUserQuery, deleteUserParams);
    }


    /*
    // 포트폴리오 좋아요
    public int updateLikes(int userIdx, int pofolIdx){
        String updateLikesQuery = "INSERT INTO PofolLike(userIdx, pofolIdx) VALUES (?,?) \n" +
                                  "UPDATE PofolLike SET status = 'ACTIVE' WHERE userIdx = ? and pofolIdx = ? ";
        Object[] updateLikesParams = new Object[]{userIdx, pofolIdx};

       this.jdbcTemplate.update(updateLikesQuery,updateLikesParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    // 포트폴리오 좋아요 취소
    //public int updateUnlikes(int userIdx, int pofolIdx){
    public int updateUnlikes(int userIdx, int pofolIdx){
        String updateUnlikesQuery = "UPDATE PofolLike SET status = 'INACTIVE' WHERE userIdx = ? and pofolIdx = ? ";
        Object[] updateUnlikesParams = new Object[]{userIdx, pofolIdx};

        return this.jdbcTemplate.update(updateUnlikesQuery,updateUnlikesParams);
    }

     */


    // 포트폴리오 좋아요
    public int updateLikes(int userIdx, int pofolIdx) {
        String updateLikesQuery = "INSERT INTO PofolLike(userIdx, pofolIdx) VALUES (?,?)";
        Object[] updateLikesParams = new Object[]{userIdx, pofolIdx};

        this.jdbcTemplate.update(updateLikesQuery, updateLikesParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 포트폴리오 좋아요 취소
    public int updateUnlikes(int userIdx, int pofolIdx) {
        String updateUnlikesQuery = "DELETE FROM PofolLike WHERE userIdx = ? and pofolIdx = ?";
        Object[] updateUnlikesParams = new Object[]{userIdx, pofolIdx};

        return this.jdbcTemplate.update(updateUnlikesQuery, updateUnlikesParams);
    }

    // 댓글 작성

    public int insertComment(int pofolIdx, int userIdx, PostCommentReq postCommentReq) {

        String insertCommentQuery = "INSERT INTO PofolComment(pofolIdx, userIdx, content) VALUES (?, ?, ?)";
        Object[] insertCommentParams = new Object[]{pofolIdx, userIdx, postCommentReq.getContent()};

        this.jdbcTemplate.update(insertCommentQuery, insertCommentParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }

    // 댓글 삭제
    public int deleteComment(int pofolCommentIdx) {
        String deleteCommentQuery = "UPDATE PofolComment SET status = 'INACTIVE' WHERE pofolCommentIdx = ? ";
        Object[] deleteCommentParams = new Object[]{pofolCommentIdx};

        return this.jdbcTemplate.update(deleteCommentQuery, deleteCommentParams);


    }
}
