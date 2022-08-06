package com.example.demo.src.chat;


import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.GetChatRoomRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ChatProvider {

    private final ChatDao chatDao;
    private final JwtService jwtService;


    @Autowired
    public ChatProvider(ChatDao chatDao, JwtService jwtService) {
        this.chatDao = chatDao;
        this.jwtService = jwtService;

    }

    /**
     *  채팅방 확인
     * */
    public int checkChatRoomExist(int userIdx, int chatRoomIdx) throws BaseException {
        try {
            return chatDao.checkChatRoomExist(userIdx, chatRoomIdx);
        } catch (Exception exception) {
            throw new BaseException(POSTS_EMPTY_CHAT_ID);
        }
    }

    /**
     * 채팅방 리스트 조회
     */
    public List<GetChatRoomRes> getChatRoom(int userIdx) throws BaseException {

        try{
            List<GetChatRoomRes> getChatRoomRes = chatDao.getChatRoom(userIdx);
            return getChatRoomRes;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
