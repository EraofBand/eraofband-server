package com.example.demo.src.chat;


import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.GetChatRoomExistReq;
import com.example.demo.src.chat.model.GetChatRoomExistRes;
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
    public int checkChatRoomExist(int userIdx, String chatRoomIdx) throws BaseException {
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


    /**
     *  첫 번째 유저가 속해 있는 채팅방 반환
     * */
    public GetChatRoomExistRes getFirstUserExist(GetChatRoomExistReq getChatRoomExistReq) throws BaseException {
        try {
            return chatDao.checkFirstUserExist(getChatRoomExistReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     *  두 번째 유저가 속해 있는 채팅방 반환
     * */
    public GetChatRoomExistRes getSecondUserExist(GetChatRoomExistReq getChatRoomExistReq) throws BaseException {
        try {
            return chatDao.checkSecondUserExist(getChatRoomExistReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     *  첫 번째 유저 채팅방 확인
     * */
    public int checkFirstExist(int firstIdx, int secondIdx) throws BaseException {
        try {
            return chatDao.checkFirstExist(firstIdx, secondIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     *  두 번째 유저 채팅방 확인
     * */
    public int checkSecondExist(int firstIdx, int secondIdx) throws BaseException {
        try {
            return chatDao.checkSecondExist(firstIdx, secondIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     *  차단 당한 유저인지 확인
     * */
    public int checkBlockedUser(int firstIdx, int secondIdx) throws BaseException {
        try {
            return chatDao.checkBlockedUser(firstIdx, secondIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
