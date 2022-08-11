package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ChatService {

    private final ChatDao chatDao;
    private final ChatProvider chatProvider;
    private final JwtService jwtService;


    private  int result;


    @Autowired
    public ChatService(ChatDao chatDao, ChatProvider chatProvider, JwtService jwtService) {
        this.chatDao = chatDao;
        this.chatProvider = chatProvider;
        this.jwtService = jwtService;
    }


    /**
     *  채팅방 생성
     * */
    public void createChatRoom(PostChatReq postChatReq) throws BaseException {

        try{
            result = chatDao.createChatRoom(postChatReq);

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(POST_FAIL_CHAT);
        }
    }








    /**
     *  채팅방 나가기
     * */
    public void deleteChatRoom(int userIdx, String chatRoomIdx) throws BaseException {

        if(chatProvider.checkChatRoomExist(userIdx, chatRoomIdx) ==0){
            throw new BaseException(POSTS_EMPTY_CHAT_ID);
        }

        try{
            result = chatDao.updateChatRoomStatus(userIdx, chatRoomIdx);

        } catch(Exception exception){
            //System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(DELETE_FAIL_CHAT);
        }
    }

}
