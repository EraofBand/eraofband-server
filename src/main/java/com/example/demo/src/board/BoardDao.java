package com.example.demo.src.board;

import com.example.demo.src.board.model.*;
import com.example.demo.src.lesson.model.GetMemberRes;
import com.example.demo.src.pofol.model.GetCommentRes;
import com.example.demo.src.pofol.model.GetPofolRes;
import com.example.demo.src.pofol.model.PatchPofolReq;
import com.example.demo.src.pofol.model.PostPofolReq;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class BoardDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 유저 확인
     * */
    public int checkUserExist(int userIdx) {
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ? and status = 'ACTIVE')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    /**
     * 게시판 게시물 확인
     * */
    public int checkBoardExist(int boardIdx) {
        String checkPostExistQuery = "select exists(select boardIdx from Board where boardIdx = ? and status = 'ACTIVE')";
        int checkPostExistParams = boardIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    /**
     * 게시물 리스트 조회
     * */
    public List<GetBoardRes> selectBoardList(int category) {
        String selectBoardListQuery = "\n" +
                "        SELECT b.boardIdx as boardIdx,\n" +
                "            u.userIdx as userIdx,\n" +
                "            b.category as category,\n" +
                "            u.nickName as nickName,\n" +
                "            b.title as title,\n" +
                "            b.imgUrl as imgUrl,\n" +
                "            IF(boardLikeCount is null, 0, boardLikeCount) as boardLikeCount,\n" +
                "            IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                "            case\n" +
                "                when timestampdiff(second, b.createdAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(second, b.createdAt, current_timestamp), '초 전')\n" +
                "                when timestampdiff(minute, b.createdAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(minute, b.createdAt, current_timestamp), '분 전')\n" +
                "                when timestampdiff(hour, b.createdAt, current_timestamp) < 24\n" +
                "                    then concat(timestampdiff(hour, b.createdAt, current_timestamp), '시간 전')\n" +
                "                when timestampdiff(day, b.createdAt, current_timestamp) < 365\n" +
                "                    then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                "                else timestampdiff(year, b.createdAt, current_timestamp)\n" +
                "            end as createdAt\n" +
                "        FROM Board as b\n" +
                "            join User as u on u.userIdx = b.userIdx\n" +
                "            left join (select boardIdx, userIdx, count(boardLikeIdx) as boardLikeCount from BoardLike WHERE status = 'ACTIVE' group by boardIdx) blc on blc.boardIdx = b.boardIdx\n" +
                "            left join (select boardIdx, count(boardCommentIdx) as commentCount from BoardComment WHERE status = 'ACTIVE' group by boardIdx) c on c.boardIdx = b.boardIdx\n" +
                "        WHERE b.category = ? and b.status = 'ACTIVE'\n" +
                "        group by b.boardIdx order by b.boardIdx DESC;\n";
        int selectBoardListParam = category;
        return this.jdbcTemplate.query(selectBoardListQuery,
                (rs, rowNum) -> new GetBoardRes(
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("category"),
                        rs.getString("title"),
                        rs.getString("imgUrl"),
                        rs.getString("nickName"),
                        rs.getInt("boardLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("createdAt")
                ), selectBoardListParam);
    }

    /**
     * 게시물 조회
     * */
    public GetBoardInfoRes selectBoardInfo(int userIdx, int boardIdx,  List<GetBoardCommentRes> getBoardComments) {
        String selectBoardInfoQuery = "\n" +
                "        SELECT b.boardIdx as boardIdx,\n" +
                "            u.userIdx as userIdx,\n" +
                "            b.category as category,\n" +
                "            u.nickName as nickName,\n" +
                "            b.title as title,\n" +
                "            b.imgUrl as imgUrl,\n" +
                "            b.content as content,\n" +
                "            b.views as views,\n" +
                "            IF(boardLikeCount is null, 0, boardLikeCount) as boardLikeCount,\n" +
                "            IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                "            case\n" +
                "                when timestampdiff(second, b.createdAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(second, b.createdAt, current_timestamp), '초 전')\n" +
                "                when timestampdiff(minute, b.createdAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(minute, b.createdAt, current_timestamp), '분 전')\n" +
                "                when timestampdiff(hour, b.createdAt, current_timestamp) < 24\n" +
                "                    then concat(timestampdiff(hour, b.createdAt, current_timestamp), '시간 전')\n" +
                "                when timestampdiff(day, b.createdAt, current_timestamp) < 365\n" +
                "                    then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                "                else timestampdiff(year, b.createdAt, current_timestamp)\n" +
                "            end as createdAt,\n" +
                "            IF(bl.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "        FROM Board as b\n" +
                "            join User as u on u.userIdx = b.userIdx\n" +
                "            left join (select boardIdx, userIdx, count(boardLikeIdx) as boardLikeCount from BoardLike WHERE status = 'ACTIVE' group by boardIdx) blc on blc.boardIdx = b.boardIdx\n" +
                "            left join (select boardIdx, count(boardCommentIdx) as commentCount from BoardComment WHERE status = 'ACTIVE' group by boardIdx) c on c.boardIdx = b.boardIdx\n" +
                "            left join BoardLike as bl on bl.userIdx = ? and bl.boardIdx = b.boardIdx\n" +
                "        WHERE b.boardIdx = ? and b.status = 'ACTIVE'\n" +
                "        group by b.boardIdx order by b.boardIdx DESC;\n";
        Object[] selectBoardInfoParam = new Object[]{userIdx, boardIdx};
        return this.jdbcTemplate.queryForObject(selectBoardInfoQuery,
                (rs, rowNum) -> new GetBoardInfoRes(
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("category"),
                        rs.getString("title"),
                        rs.getString("imgUrl"),
                        rs.getString("nickName"),
                        rs.getString("content"),
                        rs.getInt("views"),
                        rs.getInt("boardLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("createdAt"),
                        rs.getString("LikeOrNot"),
                        getBoardComments
                ), selectBoardInfoParam);
    }

    /**
     * 게시물 댓글 리스트 조회
     * */
    public List<GetBoardCommentRes> selectComment(int boardIdx) {
        String selectCommentQuery = "SELECT b.boardCommentIdx as boardCommentIdx,\n" +
                "b.boardIdx as boardIdx, \n" +
                "b.userIdx as userIdx,\n" +
                "u.nickName as nickName,\n" +
                "u.profileImgUrl as profileImgUrl,\n" +
                "b.content as content,\n" +
                "case\n" +
                "when timestampdiff(second, b.updatedAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(second, b.updatedAt, current_timestamp), '초 전')\n" +
                "when timestampdiff(minute , b.updatedAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(minute, b.updatedAt, current_timestamp), '분 전')\n" +
                "when timestampdiff(hour , b.updatedAt, current_timestamp) < 24\n" +
                "then concat(timestampdiff(hour, b.updatedAt, current_timestamp), '시간 전')\n" +
                "when timestampdiff(day , b.updatedAt, current_timestamp) < 365\n" +
                "then concat(timestampdiff(day, b.updatedAt, current_timestamp), '일 전')\n" +
                "else timestampdiff(year , b.updatedAt, current_timestamp)\n" +
                "end as updatedAt\n" +
                "FROM BoardComment as b\n" +
                "join User as u on u.userIdx = b.userIdx\n" +
                "WHERE b.boardIdx = ? and b.status = 'ACTIVE'\n " +
                "group by b.boardCommentIdx order by b.boardCommentIdx DESC; \n";
        int selectCommentParam = boardIdx;
        return this.jdbcTemplate.query(selectCommentQuery,
                (rs, rowNum) -> new GetBoardCommentRes(
                        rs.getInt("boardCommentIdx"),
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getString("updatedAt")
                ), selectCommentParam);

    }


    /**
     * 게시판 게시물 생성
     * */
    public int insertBoard(int userIdx, PostBoardReq postBoardReq) {
        String insertBoardQuery = "INSERT INTO Board(userIdx, category, content, imgUrl, title, views) VALUES (?, ?, ?, ?, ?, 0)";
        Object[] insertBoardParams = new Object[]{userIdx, postBoardReq.getCategory(), postBoardReq.getContent(), postBoardReq.getImgUrl(), postBoardReq.getTitle()};
        this.jdbcTemplate.update(insertBoardQuery, insertBoardParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }


    /**
     * 게시판 게시물 수정
     * */
    public int updateBoard(int boardIdx, PatchBoardReq patchBoardReq) {
        String updateBoardQuery = "UPDATE Board SET title = ?, content = ?, imgUrl = ? WHERE boardIdx = ? and status='ACTIVE'\n";
        Object[] updateBoardParams = new Object[]{patchBoardReq.getTitle(), patchBoardReq.getContent(), patchBoardReq.getImgUrl(), boardIdx};

        return this.jdbcTemplate.update(updateBoardQuery, updateBoardParams);
    }

    /**
     * 게시판 게시물 삭제
     * */
    public int updateBoardStatus(int boardIdx) {
        String deleteBoardQuery = "update Board b" +
                "    left join BoardComment as bc on (bc.boardIdx=b.boardIdx)\n" +
                "    left join BoardLike as bl on (bl.boardIdx=b.boardIdx)\n" +
                "        set b.status='INACTIVE',\n" +
                "            bc.status='INACTIVE',\n" +
                "            bl.status='INACTIVE'\n" +
                "   where b.boardIdx = ? ";
        Object[] deleteBoardParams = new Object[]{boardIdx};

        return this.jdbcTemplate.update(deleteBoardQuery, deleteBoardParams);
    }

    /**
     * 게시물 조회 수 증가
     * */
    public int updateBoardCount(int boardIdx) {
        String updateBoardCountQuery = "UPDATE Board SET views=views+1 WHERE boardIdx = ? and status='ACTIVE'\n";
        Object[] updateBoardCountParams = new Object[]{boardIdx};
        return this.jdbcTemplate.update(updateBoardCountQuery, updateBoardCountParams);
    }

}
