package com.example.demo.src.chat;


import com.example.demo.config.BaseException;
import com.example.demo.src.GetUserTokenRes;
import com.example.demo.src.chat.model.*;
import com.example.demo.src.lesson.model.GetMemberRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Repository
public class ChatDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }






    /**
     *  채팅방 생성
     * */
    public int createChatRoom(PostChatReq postChatReq){
        String checkChatRoomExistQuery = "INSERT INTO ChatContent (firstUserIdx, secondUserIdx, chatRoomIdx)\n" +
                "VALUES (?,?,?), (?,?,?)";
        Object[] checkChatRoomExistParams = new Object[]{ postChatReq.getFirstUserIdx(),postChatReq.getSecondUserIdx(),postChatReq.getChatRoomIdx(),postChatReq.getSecondUserIdx(),postChatReq.getFirstUserIdx(),postChatReq.getChatRoomIdx() };

        return this.jdbcTemplate.update(checkChatRoomExistQuery,checkChatRoomExistParams);
    }


    /**
     * 채팅방 확인
     * */
    public int checkChatRoomExist(int userIdx, String chatRoomIdx){
        String checkChatRoomExistQuery = "SELECT exists(SELECT chatIdx FROM ChatContent WHERE firstUserIdx = ? and chatRoomIdx = ? and status = 'ACTIVE')";
        Object[] checkChatRoomExistParams = new Object[]{ userIdx, chatRoomIdx };
        return this.jdbcTemplate.queryForObject(checkChatRoomExistQuery,
                int.class,
                checkChatRoomExistParams);
    }

    /**
     * 채팅방 리스트 조회
     */
    public List<GetChatRoomRes> getChatRoom(int userIdx) {
        String getChatRoomQuery = "SELECT c.chatRoomIdx as chatRoomIdx, u.nickName as nickName, u.profileImgUrl as profileImgUrl, c.secondUserIdx as otherUserIdx,\n" +
                "              (IF(exists(select chatIdx from ChatContent where secondUserIdx = ? and chatRoomIdx = c.chatRoomIdx and status='ACTIVE'), 1, 0)) as status\n" +
                "FROM ChatContent as c\n" +
                "JOIN User u on c.secondUserIdx=u.userIdx and (u.status='ACTIVE' or u.status='INACTIVE')\n" +
                "WHERE c.firstUserIdx=? and c.status='ACTIVE'\n" +
                "group by c.chatIdx";
        Object[] getChatRoomParams = new Object[]{ userIdx, userIdx };
        return this.jdbcTemplate.query(getChatRoomQuery,
                                       (rs, rowNum) -> new GetChatRoomRes(
                                               rs.getString("chatRoomIdx"),
                                               rs.getString("nickName"),
                                               rs.getString("profileImgUrl"),
                                               rs.getInt("status"),
                                               rs.getInt("otherUserIdx")
                                       ),
                                       getChatRoomParams);
    }

    /**
     * 채팅방 나가기
     * */
    public int updateChatRoomStatus(int userIdx, String chatRoomIdx, int lastChatIdx){
        String deleteChatRoomQuery = "update ChatContent as c\n" +
                "set c.status='INACTIVE', c.lastChatIdx=?\n" +
                "where c.firstUserIdx = ? and c.chatRoomIdx = ?";
        Object[] deleteChatRoomParams = new Object[]{ lastChatIdx, userIdx, chatRoomIdx };

        return this.jdbcTemplate.update(deleteChatRoomQuery,deleteChatRoomParams);
    }

    /**
     * 첫 번째 유저가 속해 있는 채팅방 반환
     * */
    public GetChatRoomExistRes checkFirstUserExist(GetChatRoomExistReq getChatRoomExistReq){
        String checkChatRoomExistQuery = "select chatRoomIdx,  IF(status = 'ACTIVE', 1, 0) as status from ChatContent where  firstUserIdx = ? and secondUserIdx= ?";
        Object[] checkChatRoomExistParams = new Object[]{ getChatRoomExistReq.getFirstUserIdx(), getChatRoomExistReq.getSecondUserIdx() };

        return this.jdbcTemplate.queryForObject(checkChatRoomExistQuery,
                (rs, rowNum) -> new GetChatRoomExistRes(
                        rs.getString("chatRoomIdx"),
                        rs.getInt("status")),
                checkChatRoomExistParams);
    }

    /**
     * 두 번째 유저가 속해 있는 채팅방 반환
     * */
    public GetChatRoomExistRes checkSecondUserExist(GetChatRoomExistReq getChatRoomExistReq){
        String checkChatRoomExistQuery = "select chatRoomIdx,   IF(status = 'ACTIVE', 1, 0) as status from ChatContent where  firstUserIdx = ? and secondUserIdx= ?";
        Object[] checkChatRoomExistParams = new Object[]{ getChatRoomExistReq.getSecondUserIdx(), getChatRoomExistReq.getFirstUserIdx() };

        return this.jdbcTemplate.queryForObject(checkChatRoomExistQuery,
                (rs, rowNum) -> new GetChatRoomExistRes(
                        rs.getString("chatRoomIdx"),
                        rs.getInt("status")),
                checkChatRoomExistParams);
    }

    /**
     * 첫 번째 유저 채팅방 확인
     * */
    public int checkFirstExist(int firstIdx, int secondIdx){
        String checkFirstExistQuery = "SELECT exists(SELECT chatIdx FROM ChatContent WHERE firstUserIdx = ? and secondUserIdx= ?)";
        Object[] checkFirstExistParams = new Object[]{ firstIdx, secondIdx };
        return this.jdbcTemplate.queryForObject(checkFirstExistQuery,
                int.class,
                checkFirstExistParams);
    }

    /**
     * 두 번째 유저 채팅방 확인
     * */
    public int checkSecondExist(int firstIdx, int secondIdx){
        String checkSecondExistQuery = "SELECT exists(SELECT chatIdx FROM ChatContent WHERE secondUserIdx = ? and firstUserIdx= ?)";
        Object[] checkSecondExistParams = new Object[]{ firstIdx, secondIdx };
        return this.jdbcTemplate.queryForObject(checkSecondExistQuery,
                int.class,
                checkSecondExistParams);
    }

    /**
     * 채팅방 활성화
     * */
    public int activeChatroom(int userIdx, String chatRoomIdx){
        String activeChatRoomQuery = "update ChatContent as c\n" +
                "set c.status='ACTIVE'\n" +
                "where (c.firstUserIdx =? or c.secondUserIdx = ?) and c.chatRoomIdx = ?";
        Object[] activeChatRoomParams = new Object[]{ userIdx, userIdx, chatRoomIdx };

        return this.jdbcTemplate.update(activeChatRoomQuery, activeChatRoomParams);
    }

    /**
     * 채팅방 들어가기, 마지막 채팅 인덱스 반환
     * */
    public GetChatRoomInRes getChatRoomIn(int userIdx, int chatRoomIdx){
        String getChatRoomInQuery = "select lastChatIdx from ChatContent where chatRoomIdx=? and firstUserIdx=?";
        Object[] getChatRoomInParams = new Object[]{chatRoomIdx, userIdx};

        return this.jdbcTemplate.queryForObject(getChatRoomInQuery,
                (rs, rowNum) -> new GetChatRoomInRes(
                        rs.getInt("lastChatIdx")),
                getChatRoomInParams);
    }

    /**
     * 차단 상태 확인
     * */
    public int checkBlockState(int firstIdx, int secondIdx){
        String checkSecondExistQuery = "SELECT exists(SELECT blockIdx FROM Block WHERE ( blockedIdx = ? AND blockerIdx= ?) OR (blockerIdx = ? AND blockedIdx = ?) );";
        Object[] checkSecondExistParams = new Object[]{ firstIdx, secondIdx, firstIdx, secondIdx };
        return this.jdbcTemplate.queryForObject(checkSecondExistQuery,
                                                int.class,
                                                checkSecondExistParams);
    }

}
