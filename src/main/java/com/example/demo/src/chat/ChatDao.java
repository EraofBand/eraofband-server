package com.example.demo.src.chat;


import com.example.demo.config.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
