package com.example.demo.src.chat;


import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.GetChatRoomRes;
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
     * 채팅방 확인
     * */
    public int checkChatRoomExist(int userIdx, int chatRoomIdx){
        String checkChatRoomExistQuery = "SELECT exists(SELECT chatIdx FROM ChatContent WHERE chatUserIdx = ? and chatRoomIdx = ? and status = 'ACTIVE')";
        Object[] checkChatRoomExistParams = new Object[]{ userIdx, chatRoomIdx };
        return this.jdbcTemplate.queryForObject(checkChatRoomExistQuery,
                int.class,
                checkChatRoomExistParams);
    }

    /**
     * 채팅방 리스트 조회
     */
    public List<GetChatRoomRes> getChatRoom(int userIdx) {
        String getChatRoomQuery = "SELECT c.chatRoomIdx as chatRoomIdx,\n" +
                "       if(u.status='ACTIVE', nickName, null) as nickName,\n" +
                "       if(u.status='ACTIVE', profileImgUrl, null) as profileImgUrl\n" +
                "FROM User u\n" +
                "    JOIN ChatContent c on c.chatUserIdx=? and c.status='ACTIVE'\n" +
                "    JOIN ChatContent t on t.chatRoomIdx=c.chatRoomIdx and t.chatUserIdx!=c.chatUserIdx\n" +
                "WHERE u.userIdx = t.chatUserIdx";
        Object[] getChatRoomParams = new Object[]{ userIdx };
        return this.jdbcTemplate.query(getChatRoomQuery,
                                       (rs, rowNum) -> new GetChatRoomRes(
                                               rs.getInt("chatRoomIdx"),
                                               rs.getString("nickName"),
                                               rs.getString("profileImgUrl")
                                       ),
                                       getChatRoomParams);
    }

    /**
     * 채팅방 삭제
     * */
    public int updateChatRoomStatus(int userIdx, int chatRoomIdx){
        String deleteChatRoomQuery = "update ChatContent as c\n" +
                "set c.status='INACTIVE'\n" +
                "where c.chatUserIdx = ? and c.chatRoomIdx = ?";
        Object[] deleteChatRoomParams = new Object[]{ userIdx, chatRoomIdx };

        return this.jdbcTemplate.update(deleteChatRoomQuery,deleteChatRoomParams);
    }






}
