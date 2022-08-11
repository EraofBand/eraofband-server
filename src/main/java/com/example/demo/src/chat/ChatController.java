package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.GetChatRoomRes;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.lesson.model.PostLessonReq;
import com.example.demo.src.lesson.model.PostLessonRes;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private final ChatProvider chatProvider;
    @Autowired
    private final ChatService chatService;
    @Autowired
    private final JwtService jwtService;

    public ChatController(ChatProvider chatProvider, ChatService chatService, JwtService jwtService){
        this.chatProvider = chatProvider;
        this.chatService = chatService;
        this.jwtService = jwtService;
    }

    /**
     * 채팅방 리스트 조회 API
     * [GET] /chat
     * @return BaseResponse<List<GetChatRoomRes>>
     */
    @ResponseBody
    @GetMapping("/chat-room") // (get) https://eraofband.shop/chat
    @ApiOperation(value = "채팅방 리스트 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetChatRoomRes>> getChatRoom(){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            List<GetChatRoomRes> getChatRoomRes = chatProvider.getChatRoom(userIdxByJwt);
            return new BaseResponse<>(getChatRoomRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 채팅방 생성 API
     * [POST] /chat
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/chat
    @ApiOperation(value = "채팅방 생성 처리")
    @ApiResponses({
            @ApiResponse(code=2092, message="채팅방 생성에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> createChatRoom(@RequestBody PostChatReq postChatReq) {
        try {

            chatService.createChatRoom(postChatReq);

            String result = "채팅방 생성이 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 채팅방 나가기 API
     * [PATCH] /chat/status/{chatRoomIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{chatRoomIdx}") // (patch) https://eraofband.shop/chat/status/2
    @ApiOperation(value = "채팅 나가기 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="chatRoomIdx", value="나갈 채팅방 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2090, message="채팅방 아이디 값을 확인해주세요."),
            @ApiResponse(code=2091, message="채팅방 삭제에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deleteChatRoom(@PathVariable("chatRoomIdx") String chatRoomIdx){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            chatService.deleteChatRoom(userIdxByJwt, chatRoomIdx);

            String result = "채팅방 나가기가 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
