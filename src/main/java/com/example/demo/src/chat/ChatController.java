package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.GetChatRoomExistReq;
import com.example.demo.src.chat.model.GetChatRoomExistRes;
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
     * [GET] /chat/chat-room
     * @return BaseResponse<List<GetChatRoomRes>>
     */
    @ResponseBody
    @GetMapping("/chat-room") // (get) https://eraofband.shop/chat/chat-room
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

    /**
     * 채팅방 유무 여부 조회 API
     * [PATCH] /chat/chat-room/exist
     * @return BaseResponse<List<GetChatRoomRes>>
     */
    @ResponseBody
    @PatchMapping("/chat-room/exist") // (patch) https://eraofband.shop/chat/chat-room/exist
    @ApiOperation(value = "1대1 채팅 버튼", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetChatRoomExistRes> getChatRoomExist(@RequestBody GetChatRoomExistReq getChatRoomExistReq){
        try{
            //첫 번째 유저 존재 확인
            int first=chatProvider.checkFirstExist(getChatRoomExistReq.getFirstUserIdx(), getChatRoomExistReq.getSecondUserIdx());
            //두 번째 유저 존재 확인
            int second= chatProvider.checkSecondExist(getChatRoomExistReq.getFirstUserIdx(), getChatRoomExistReq.getSecondUserIdx());

            GetChatRoomExistRes getChatRoomExistRes = new GetChatRoomExistRes();
            if(first==0 && second==0){
                getChatRoomExistRes.setChatRoomIdx(null);
                getChatRoomExistRes.setStatus(0);
            }
            else {
                //첫 번째 유저 정보
                GetChatRoomExistRes getFirst=chatProvider.getFirstUserExist(getChatRoomExistReq);
                //두 번째 유저 정보
                GetChatRoomExistRes getSecond=chatProvider.getSecondUserExist(getChatRoomExistReq);

                //두 명이 모두 속한 채팅방이 비활성화인 경우
                if (getFirst.getStatus() == 0 && getSecond.getStatus() == 0) {
                    getChatRoomExistRes.setChatRoomIdx(getFirst.getChatRoomIdx());
                    getChatRoomExistRes.setStatus(getSecond.getStatus());
                    //첫 채팅 보낼 때 활성화 필요
                }

                //첫 번째 유저만 나간 경우
                else if (getFirst.getStatus() == 0 && getSecond.getStatus() == 1) {
                    //두 번째 유저가 속해 있는 채팅방 반환
                    getChatRoomExistRes.setChatRoomIdx(getSecond.getChatRoomIdx());
                    getChatRoomExistRes.setStatus(getSecond.getStatus());
                    //active로 만들기
                    chatService.activeChatroom(getChatRoomExistReq.getFirstUserIdx(), getSecond.getChatRoomIdx());
                }

                //두 번째 유저만 나간 경우
                else if (getFirst.getStatus() == 1 && getSecond.getStatus() == 0) {
                    getChatRoomExistRes.setChatRoomIdx(getFirst.getChatRoomIdx());
                    getChatRoomExistRes.setStatus(getSecond.getStatus());
                    //첫 채팅 보낼 때 활성화 필요
                }

                //두 명이 속한 채팅방이 있는 경우
                else {
                    getChatRoomExistRes.setChatRoomIdx(getFirst.getChatRoomIdx());
                    getChatRoomExistRes.setStatus(getSecond.getStatus());
                }
            }

            return new BaseResponse<>(getChatRoomExistRes);

        } catch (BaseException exception){
            System.out.println(exception);
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 채팅방 다시 활성화 API
     * [PATCH] /chat/status/active
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/active") // (patch) https://eraofband.shop/chat/status/active
    @ApiOperation(value = "채팅방 다시 활성화", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2090, message="채팅방 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> activeChatRoom(@RequestBody PostChatReq postChatReq){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            chatService.activeChatroom(postChatReq.getSecondUserIdx(), postChatReq.getChatRoomIdx());

            String result = "상대방이 채팅이 활성화 되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
