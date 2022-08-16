package com.example.demo.src.board;

import com.example.demo.src.GetUserTokenRes;
import com.example.demo.src.board.model.*;
import com.example.demo.src.lesson.model.GetMemberRes;
import com.example.demo.src.pofol.model.*;
import com.example.demo.src.session.model.GetSessionMemRes;
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
     */
    public int checkUserExist(int userIdx) {
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ? and status = 'ACTIVE')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    /**
     * 게시판 게시물 확인
     */
    public int checkBoardExist(int boardIdx) {
        String checkPostExistQuery = "select exists(select boardIdx from Board where boardIdx = ? and status = 'ACTIVE')";
        int checkPostExistParams = boardIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    /**
     * 게시글 댓글 확인
     */
    public int checkCommentExist(int boardCommentIdx) {
        String checkCommentExistQuery = "select exists(select boardCommentIdx from BoardComment where boardCommentIdx = ? and status = 'ACTIVE')";
        int checkCommentExistParams = boardCommentIdx;
        return this.jdbcTemplate.queryForObject(checkCommentExistQuery,
                int.class,
                checkCommentExistParams);

    }

    /**
     * 게시글 대댓글 여부 확인
     */
    public int checkReplyExist(int boardCommentIdx) {
        String checkReplyExistQuery = "SELECT exists(SELECT boardCommentIdx FROM BoardComment WHERE groupNum=? and classNum=1 and status='ACTIVE')";
        int checkReplyExistParams = boardCommentIdx;
        return this.jdbcTemplate.queryForObject(checkReplyExistQuery,
                int.class,
                checkReplyExistParams);

    }

    /**
     * 게시글 좋아요 중복 확인
     */
    public int checkBoardLiked(int userIdx, int boardIdx) {
        String checkBoardLikedQuery = "SELECT exists(SELECT boardLikeIdx FROM BoardLike WHERE userIdx=? and boardIdx=?)";
        Object[] checkBoardLikedParams = new Object[]{userIdx, boardIdx};
        return this.jdbcTemplate.queryForObject(checkBoardLikedQuery,
                int.class,
                checkBoardLikedParams);

    }

    /**
     * 댓글 작성
     */
    public int insertComment(int boardIdx, int userIdx, PostBoardCommentReq postBoardCommentReq) {

        String insertCommentQuery = "INSERT INTO BoardComment(boardIdx, userIdx, content, classNum) VALUES (?, ?, ?, 0)";

        Object[] insertCommentParams = new Object[]{boardIdx, userIdx, postBoardCommentReq.getContent()};

        this.jdbcTemplate.update(insertCommentQuery, insertCommentParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     * 대댓글 작성
     */
    public int insertReComment(int boardIdx, int userIdx, PostBoardCommentReq postBoardCommentReq) {

        String insertCommentQuery = "INSERT INTO BoardComment(boardIdx, userIdx, content, classNum, groupNum) VALUES (?, ?, ?, 1, ?)";

        Object[] insertCommentParams = new Object[]{boardIdx, userIdx, postBoardCommentReq.getContent(), postBoardCommentReq.getGroupNum()};

        this.jdbcTemplate.update(insertCommentQuery, insertCommentParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }


    /**
     * 댓글 그룹 추가
     */
    public int insertCommentGroup(int boardCommentIdx) {
        String insertCommentGroupQuery = "UPDATE BoardComment SET groupNum = ? WHERE boardCommentIdx = ? ";
        Object[] insertCommentGroupParams = new Object[]{boardCommentIdx, boardCommentIdx};

        return this.jdbcTemplate.update(insertCommentGroupQuery, insertCommentGroupParams);

    }

    /**
     * 댓글 삭제
     */
    public int deleteComment(int boardCommentIdx) {
        String deleteCommentQuery = "UPDATE BoardComment SET status = 'INACTIVE' WHERE boardCommentIdx = ? ";
        Object[] deleteCommentParams = new Object[]{boardCommentIdx};

        return this.jdbcTemplate.update(deleteCommentQuery, deleteCommentParams);

    }

    /**
     * 댓글 조회
     */
    public GetBoardCommentRes certainComment(int boardCommentIdx) {

        String selectCommentQuery = "SELECT b.boardCommentIdx as boardCommentIdx,\n" +
                "b.boardIdx as boardIdx, \n" +
                "b.userIdx as userIdx,\n" +
                "u.nickName as nickName,\n" +
                "u.profileImgUrl as profileImgUrl,\n" +
                "b.content as content,\n" +
                "b.classNum as classNum,\n" +
                "b.groupNum as groupNum,\n" +
                "case\n" +
                "when timestampdiff(second, b.createdAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(second, b.createdAt, current_timestamp), '초 전')\n" +
                "when timestampdiff(minute , b.createdAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(minute, b.createdAt, current_timestamp), '분 전')\n" +
                "when timestampdiff(hour , b.createdAt, current_timestamp) < 24\n" +
                "then concat(timestampdiff(hour, b.createdAt, current_timestamp), '시간 전')\n" +
                "when timestampdiff(day , b.createdAt, current_timestamp) < 7\n" +
                "then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                "else date_format(b.createdAt, '%Y.%m.%d.')\n" +
                "end as updatedAt\n" +
                "FROM BoardComment as b\n" +
                "join User as u on u.userIdx = b.userIdx\n" +
                "WHERE b.boardCommentIdx = ? and b.status = 'ACTIVE'\n " +
                "group by b.boardCommentIdx order by b.boardCommentIdx; \n";

        int selectCommentParam = boardCommentIdx;
        return this.jdbcTemplate.queryForObject(selectCommentQuery,
                (rs, rowNum) -> new GetBoardCommentRes(
                        rs.getInt("boardCommentIdx"),
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getInt("classNum"),
                        rs.getInt("groupNum"),
                        rs.getString("updatedAt")
                ), selectCommentParam);

    }

    /**
     * 게시물 리스트 조회
     */
    public List<GetBoardRes> selectBoardList(int category, int boardIdx) {

        String selectBoardListQuery = "";
        Object[] selectBoardListParam = new Object[]{};

        if (boardIdx != 0) {

            selectBoardListQuery = "\n" +
                    "SELECT b.boardIdx as boardIdx,\n" +
                    "                            u.userIdx as userIdx,\n" +
                    "                            b.category as category,\n" +
                    "                            u.nickName as nickName,\n" +
                    "                            bi.imgUrl as imgUrl,\n" +
                    "                            b.title as title,\n" +
                    "                            b.content as content,\n" +
                    "                            b.views as views,\n" +
                    "                            IF(boardLikeCount is null, 0, boardLikeCount) as boardLikeCount,\n" +
                    "                            IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                    "                            case\n" +
                    "                                when timestampdiff(second, b.createdAt, current_timestamp) < 60\n" +
                    "                                    then concat(timestampdiff(second, b.createdAt, current_timestamp), '초 전')\n" +
                    "                                when timestampdiff(minute, b.createdAt, current_timestamp) < 60\n" +
                    "                                    then concat(timestampdiff(minute, b.createdAt, current_timestamp), '분 전')\n" +
                    "                                when timestampdiff(hour, b.createdAt, current_timestamp) < 24\n" +
                    "                                    then concat(timestampdiff(hour, b.createdAt, current_timestamp), '시간 전')\n" +
                    "                                when timestampdiff(day, b.createdAt, current_timestamp) < 7\n" +
                    "                                    then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                    "                                else date_format(b.createdAt, '%Y.%m.%d.')\n" +
                    "                            end as updatedAt\n" +
                    "                        FROM Board as b\n" +
                    "                            join User as u on u.userIdx = b.userIdx\n" +
                    "                            left join (select boardIdx, userIdx, count(boardLikeIdx) as boardLikeCount from BoardLike WHERE status = 'ACTIVE' group by boardIdx) blc on blc.boardIdx = b.boardIdx\n" +
                    "                            left join (select boardIdx, count(boardCommentIdx) as commentCount from BoardComment WHERE status = 'ACTIVE' group by boardIdx) c on c.boardIdx = b.boardIdx\n" +
                    "                            left join BoardImage as bi on bi.boardIdx=b.boardIdx and bi.status='ACTIVE'\n" +
                    "                        WHERE b.category = ? and b.status = 'ACTIVE' and b.boardIdx<?\n" +
                    "                        group by b.boardIdx order by b.boardIdx DESC LIMIT 20;";

            selectBoardListParam = new Object[]{category, boardIdx};
        } else {
            selectBoardListQuery = "\n" +
                    "SELECT b.boardIdx as boardIdx,\n" +
                    "                            u.userIdx as userIdx,\n" +
                    "                            b.category as category,\n" +
                    "                            u.nickName as nickName,\n" +
                    "                            bi.imgUrl as imgUrl,\n" +
                    "                            b.title as title,\n" +
                    "                            b.content as content,\n" +
                    "                            b.views as views,\n" +
                    "                            IF(boardLikeCount is null, 0, boardLikeCount) as boardLikeCount,\n" +
                    "                            IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                    "                            case\n" +
                    "                                when timestampdiff(second, b.createdAt, current_timestamp) < 60\n" +
                    "                                    then concat(timestampdiff(second, b.createdAt, current_timestamp), '초 전')\n" +
                    "                                when timestampdiff(minute, b.createdAt, current_timestamp) < 60\n" +
                    "                                    then concat(timestampdiff(minute, b.createdAt, current_timestamp), '분 전')\n" +
                    "                                when timestampdiff(hour, b.createdAt, current_timestamp) < 24\n" +
                    "                                    then concat(timestampdiff(hour, b.createdAt, current_timestamp), '시간 전')\n" +
                    "                                when timestampdiff(day, b.createdAt, current_timestamp) < 7\n" +
                    "                                    then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                    "                                else date_format(b.createdAt, '%Y.%m.%d.')\n" +
                    "                            end as updatedAt\n" +
                    "                        FROM Board as b\n" +
                    "                            join User as u on u.userIdx = b.userIdx\n" +
                    "                            left join (select boardIdx, userIdx, count(boardLikeIdx) as boardLikeCount from BoardLike WHERE status = 'ACTIVE' group by boardIdx) blc on blc.boardIdx = b.boardIdx\n" +
                    "                            left join (select boardIdx, count(boardCommentIdx) as commentCount from BoardComment WHERE status = 'ACTIVE' group by boardIdx) c on c.boardIdx = b.boardIdx\n" +
                    "                            left join BoardImage as bi on bi.boardIdx=b.boardIdx and bi.status='ACTIVE'\n" +
                    "                        WHERE b.category = ? and b.status = 'ACTIVE' \n" +
                    "                        group by b.boardIdx order by b.boardIdx DESC LIMIT 20;";

            selectBoardListParam = new Object[]{category};
        }
            return this.jdbcTemplate.query(selectBoardListQuery,
                    (rs, rowNum) -> new GetBoardRes(
                            rs.getInt("boardIdx"),
                            rs.getInt("userIdx"),
                            rs.getInt("category"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("nickName"),
                            rs.getString("imgUrl"),
                            rs.getInt("views"),
                            rs.getInt("boardLikeCount"),
                            rs.getInt("commentCount"),
                            rs.getString("updatedAt")
                    ), selectBoardListParam);

    }



    /**
     * 게시물 조회
     * */
    public GetBoardInfoRes selectBoardInfo(int userIdx, int boardIdx,  List<GetBoardImgsRes> getBoardImgsRes, List<GetBoardCommentRes> getBoardComments) {
        String selectBoardInfoQuery = "\n" +
                "SELECT b.boardIdx as boardIdx,\n" +
                "                            u.userIdx as userIdx,\n" +
                "                            b.category as category,\n" +
                "                            u.nickName as nickName,\n" +
                "                            u.profileImgUrl as profileImgUrl,\n" +
                "                            b.title as title,\n" +
                "                            b.content as content,\n" +
                "                            b.views as views,\n" +
                "                            IF(boardLikeCount is null, 0, boardLikeCount) as boardLikeCount,\n" +
                "                            IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                "                            case\n" +
                "                                when timestampdiff(second, b.createdAt, current_timestamp) < 60\n" +
                "                                    then concat(timestampdiff(second, b.createdAt, current_timestamp), '초 전')\n" +
                "                                when timestampdiff(minute, b.createdAt, current_timestamp) < 60\n" +
                "                                    then concat(timestampdiff(minute, b.createdAt, current_timestamp), '분 전')\n" +
                "                                when timestampdiff(hour, b.createdAt, current_timestamp) < 24\n" +
                "                                    then concat(timestampdiff(hour, b.createdAt, current_timestamp), '시간 전')\n" +
                "                                when timestampdiff(day, b.createdAt, current_timestamp) < 7\n" +
                "                                    then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                "                                else date_format(b.createdAt, '%Y.%m.%d.  %h:%i')\n" +
                "                           end as updatedAt,\n" +
                "                           IF(bl.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "                        FROM Board as b\n" +
                "                            join User as u on u.userIdx = b.userIdx\n" +
                "                            left join (select boardIdx, userIdx, count(boardLikeIdx) as boardLikeCount from BoardLike WHERE status = 'ACTIVE' group by boardIdx) blc on blc.boardIdx = b.boardIdx\n" +
                "                            left join (select boardIdx, count(boardCommentIdx) as commentCount from BoardComment WHERE status = 'ACTIVE' group by boardIdx) c on c.boardIdx = b.boardIdx\n" +
                "                            left join BoardLike as bl on bl.userIdx = ? and bl.boardIdx = b.boardIdx\n" +
                "                        WHERE b.boardIdx = ? and b.status = 'ACTIVE'\n" +
                "                        group by b.boardIdx order by b.boardIdx DESC;\n";
        Object[] selectBoardInfoParam = new Object[]{userIdx, boardIdx};
        return this.jdbcTemplate.queryForObject(selectBoardInfoQuery,
                (rs, rowNum) -> new GetBoardInfoRes(
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("category"),
                        rs.getString("title"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        getBoardImgsRes,
                        rs.getInt("views"),
                        rs.getInt("boardLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("LikeOrNot"),
                        getBoardComments
                ), selectBoardInfoParam);
    }

    /**
     * 게시물 이미지 조회
     */
    public List<GetBoardImgsRes> getBoardImgsRes (int boardIdx) {
        String getBoardImgsQuery = "SELECT boardImgIdx,imgUrl\n" +
                "FROM BoardImage as b\n" +
                "WHERE boardIdx = ? and status = 'ACTIVE'";
        int getBoardImgsParams = boardIdx;
        return this.jdbcTemplate.query(getBoardImgsQuery,
                (rs, rowNum) -> new GetBoardImgsRes(
                        rs.getInt("boardImgIdx"),
                        rs.getString("imgUrl")),
                getBoardImgsParams);
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
                "b.classNum as classNum,\n" +
                "b.groupNum as groupNum,\n" +
                "case\n" +
                "when timestampdiff(second, b.createdAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(second, b.createdAt, current_timestamp), '초 전')\n" +
                "when timestampdiff(minute , b.createdAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(minute, b.createdAt, current_timestamp), '분 전')\n" +
                "when timestampdiff(hour , b.createdAt, current_timestamp) < 24\n" +
                "then concat(timestampdiff(hour, b.createdAt, current_timestamp), '시간 전')\n" +
                "when timestampdiff(day , b.createdAt, current_timestamp) < 7\n" +
                "then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                "else date_format(b.createdAt, '%Y.%m.%d.')\n" +
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
                        rs.getInt("classNum"),
                        rs.getInt("groupNum"),
                        rs.getString("updatedAt")
                ), selectCommentParam);

    }


    /**
     * 게시판 게시물 생성
     * */
    public int insertBoard(int userIdx, PostBoardReq postBoardReq) {
        String insertBoardQuery = "INSERT INTO Board(userIdx, category, content, title, views) VALUES (?, ?, ?, ?, 0)";
        Object[] insertBoardParams = new Object[]{userIdx, postBoardReq.getCategory(), postBoardReq.getContent(), postBoardReq.getTitle()};
        this.jdbcTemplate.update(insertBoardQuery, insertBoardParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }

    /**
     * 게시판 게시물 이미지 생성
     * */
    public int insertBoardImgs(int boardIdx, PostImgsUrlReq postImgsUrl) {
        String insertBoardImgsQuery = "INSERT INTO BoardImage(boardIdx, imgUrl) VALUES (?, ?)";
        Object[] insertBoardImgsParams = new Object[]{boardIdx,postImgsUrl.getImgUrl()};
        this.jdbcTemplate.update(insertBoardImgsQuery, insertBoardImgsParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }

    /**
     * 게시판 게시물 이미지 삭제
     * */
    public int deleteBoardImg(int boardImgIdx) {
        String deleteBoardImgQuery = "update BoardImage BI set BI.status='INACTIVE' where BI.boardImgIdx = ? ";
        Object[] deleteBoardImgParams = new Object[]{boardImgIdx};

        return this.jdbcTemplate.update(deleteBoardImgQuery, deleteBoardImgParams);
    }


    /**
     * 게시판 게시물 수정
     * */
    public int updateBoard(int boardIdx, PatchBoardReq patchBoardReq) {
        String updateBoardQuery = "UPDATE Board SET title = ?, content = ? WHERE boardIdx = ? and status='ACTIVE'\n";
        Object[] updateBoardParams = new Object[]{patchBoardReq.getTitle(), patchBoardReq.getContent(), boardIdx};

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

    /**
     * 댓글 작성자의 정보 얻기
     */
    public GetBoardComNotiInfoRes Noti(int boardCommentIdx){
        String getInfoQuery = "SELECT bc.boardCommentIdx as boardCommentIdx,\n" +
                "                b.userIdx as receiverIdx,\n" +
                "                bc.boardIdx as boardIdx,\n" +
                "                bc.userIdx as userIdx,\n" +
                "                u.nickName as nickName,\n" +
                "                u.profileImgUrl as profileImgUrl,\n" +
                "                b.title as title\n" +
                "FROM BoardComment as bc\n" +
                "                join User as u on u.userIdx = bc.userIdx\n" +
                "left join Board as b on b.boardIdx = bc.boardIdx\n" +
                "                WHERE bc.boardCommentIdx = ? and bc.status = 'ACTIVE'\n" +
                "                group by bc.boardCommentIdx order by bc.boardCommentIdx";
        int getInfoParams = boardCommentIdx;
        return this.jdbcTemplate.queryForObject(getInfoQuery,
                (rs, rowNum) -> new GetBoardComNotiInfoRes(
                        rs.getInt("boardCommentIdx"),
                        rs.getInt("receiverIdx"),
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("title")
                ),
                getInfoParams);
    }

    /**
     * 대댓글 작성자의 정보 얻기
     */
    public GetBoardComNotiInfoRes NotiRe(int boardReCommentIdx, int boardCommentIdx){
        String getReInfoQuery = "SELECT bc.boardCommentIdx as boardCommentIdx,\n" +
                "       (select userIdx as receiverIdx from BoardComment where boardCommentIdx = ?) as receiverIdx,\n" +
                "                                bc.boardIdx as boardIdx,\n" +
                "                                bc.userIdx as userIdx,\n" +
                "                                u.nickName as nickName,\n" +
                "                                u.profileImgUrl as profileImgUrl,\n" +
                "                                b.title as title\n" +
                "                FROM BoardComment as bc\n" +
                "                                join User as u on u.userIdx = bc.userIdx\n" +
                "                left join Board as b on b.boardIdx = bc.boardIdx\n" +
                "                WHERE bc.boardCommentIdx = ? and bc.status = 'ACTIVE'\n" +
                "                                group by bc.boardCommentIdx order by bc.boardCommentIdx";
        Object[] getReInfoParams = new Object[]{boardCommentIdx, boardReCommentIdx};
        return this.jdbcTemplate.queryForObject(getReInfoQuery,
                (rs, rowNum) -> new GetBoardComNotiInfoRes(
                        rs.getInt("boardCommentIdx"),
                        rs.getInt("receiverIdx"),
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("title")
                ),
                getReInfoParams);
    }

    /**
     * 알림 테이블에 추가
     */
    public void CommentNoti(GetBoardComNotiInfoRes getBoardComNotiInfoRes){

        String updateComNotiQuery = "INSERT INTO Notice(receiverIdx, image, head, body) VALUES (?,?,?,?)";
        Object[] updateComNotiParams = new Object[]{getBoardComNotiInfoRes.getReceiverIdx(), getBoardComNotiInfoRes.getProfileImgUrl(),"게시물 댓글",
                getBoardComNotiInfoRes.getNickName()+"님이 "+ getBoardComNotiInfoRes.getTitle()+"에 댓글을 남기셨습니다."};

        this.jdbcTemplate.update(updateComNotiQuery, updateComNotiParams);
    }

    /**
     * 대댓글 알림 테이블에 추가
     */
    public void CommentReNoti(GetBoardComNotiInfoRes getBoardComNotiInfoRes){

        String updateComNotiQuery = "INSERT INTO Notice(receiverIdx, image, head, body) VALUES (?,?,?,?)";
        Object[] updateComNotiParams = new Object[]{getBoardComNotiInfoRes.getReceiverIdx(), getBoardComNotiInfoRes.getProfileImgUrl(),getBoardComNotiInfoRes.getTitle()+" 답글",
                getBoardComNotiInfoRes.getNickName()+"님이 회원님의 댓글에 답글을 남기셨습니다."};

        this.jdbcTemplate.update(updateComNotiQuery, updateComNotiParams);
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

    /**
     * 게시물 좋아요
     * */
    public int updateLikes(int userIdx, int boardIdx) {
        String updateLikesQuery = "INSERT INTO BoardLike(userIdx, boardIdx) VALUES (?,?)";
        Object[] updateLikesParams = new Object[]{userIdx, boardIdx};

        this.jdbcTemplate.update(updateLikesQuery, updateLikesParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     * 작성 게시물 리스트 조회
     * */
    public List<GetMyBoardRes> selectMyBoardList(int userIdx) {
        String selectMyBoardListQuery = "\n" +
                "        SELECT b.boardIdx as boardIdx,\n" +
                "            b.userIdx as userIdx,\n" +
                "            b.category as category,\n" +
                "            b.title as title,\n" +
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
                "                when timestampdiff(day, b.createdAt, current_timestamp) < 7\n" +
                "                    then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                "                else date_format(b.createdAt, '%Y.%m.%d.')\n" +
                "            end as updatedAt\n" +
                "        FROM Board as b\n" +
                "            left join (select boardIdx, userIdx, count(boardLikeIdx) as boardLikeCount from BoardLike WHERE status = 'ACTIVE' group by boardIdx) blc on blc.boardIdx = b.boardIdx\n" +
                "            left join (select boardIdx, count(boardCommentIdx) as commentCount from BoardComment WHERE status = 'ACTIVE' group by boardIdx) c on c.boardIdx = b.boardIdx\n" +
                "        WHERE b.userIdx = ? and b.status = 'ACTIVE'\n" +
                "        group by b.boardIdx order by b.boardIdx DESC;\n";
        int selectMyBoardListParam = userIdx;
        return this.jdbcTemplate.query(selectMyBoardListQuery,
                                       (rs, rowNum) -> new GetMyBoardRes(
                                               rs.getInt("boardIdx"),
                                               rs.getInt("category"),
                                               rs.getString("title"),
                                               rs.getInt("views"),
                                               rs.getInt("boardLikeCount"),
                                               rs.getInt("commentCount"),
                                               rs.getString("updatedAt")
                                       ), selectMyBoardListParam);
    }

    /**
     * 댓글 단 게시물 리스트 조회
     * */
    public List<GetMyBoardRes> selectMyCommentBoardList(int userIdx) {
        String selectMyCommentBoardListQuery = "\n" +
                "        SELECT b.boardIdx as boardIdx,\n" +
                "            b.userIdx as userIdx,\n" +
                "            b.category as category,\n" +
                "            b.title as title,\n" +
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
                "                when timestampdiff(day, b.createdAt, current_timestamp) < 7\n" +
                "                    then concat(timestampdiff(day, b.createdAt, current_timestamp), '일 전')\n" +
                "                else date_format(b.createdAt, '%Y.%m.%d.')\n" +
                "            end as updatedAt\n" +
                "        FROM Board as b\n" +
                "            left join (select boardIdx, userIdx, count(boardLikeIdx) as boardLikeCount from BoardLike WHERE status = 'ACTIVE' group by boardIdx) blc on blc.boardIdx = b.boardIdx\n" +
                "            left join (select boardIdx, count(boardCommentIdx) as commentCount from BoardComment WHERE status = 'ACTIVE' group by boardIdx) c on c.boardIdx = b.boardIdx\n" +
                "            join(SELECT boardIdx, userIdx FROM BoardComment) as BC on BC.userIdx = ?\n" +
                "        WHERE b.boardIdx = BC.boardIdx and b.status = 'ACTIVE'\n" +
                "        group by b.boardIdx order by b.boardIdx DESC;\n";
        int selectMyCommentBoardListParam = userIdx;
        return this.jdbcTemplate.query(selectMyCommentBoardListQuery,
                                       (rs, rowNum) -> new GetMyBoardRes(
                                               rs.getInt("boardIdx"),
                                               rs.getInt("category"),
                                               rs.getString("title"),
                                               rs.getInt("views"),
                                               rs.getInt("boardLikeCount"),
                                               rs.getInt("commentCount"),
                                               rs.getString("updatedAt")
                                       ), selectMyCommentBoardListParam);
    }

    /**
     * 게시물 좋아요 취소
     * */
    public int updateUnlikes(int userIdx, int boardIdx) {
        String updateUnlikesQuery = "DELETE FROM BoardLike WHERE userIdx = ? and boardIdx = ?";
        Object[] updateUnlikesParams = new Object[]{userIdx, boardIdx};

        return this.jdbcTemplate.update(updateUnlikesQuery, updateUnlikesParams);
    }

}
