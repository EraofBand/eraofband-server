package com.example.demo.src.user;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }
    /**
     * 회원 조회 API
     * [GET] /users
     * 이메일 검색 조회 API
     * [GET] /users? Email=
     * @return BaseResponse<GetUserRes>
     */
    //Query String
//    @ResponseBody
//    @GetMapping("") // (GET) 127.0.0.1:9000/users
//    public BaseResponse<GetUserRes> getUsers(@RequestParam(required = true) String Email) {
//        try{
//            // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
//            if(Email.length()==0){
//                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
//            }
//            // 이메일 정규표현
//            if(!isRegexEmail(Email)){
//                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
//            }
//            GetUserRes getUsersRes = userProvider.getUsersByEmail(Email);
//            return new BaseResponse<>(getUsersRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
    /**
     * 다른 회원 페이지 조회 API
     * [GET] /users/{userIdx}
     * 유저 인덱스 검색 조회 API
     * @return BaseResponse<GetUserFeedRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/users/userIdx
    @ApiOperation(value = "다른 회원 정보 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<GetUserFeedRes> getUserByIdx(@PathVariable("userIdx")int userIdx) {
            try{
                int myId = jwtService.getUserIdx();
                GetUserFeedRes getUserFeed = userProvider.getUserByIdx(myId,userIdx);
                return new BaseResponse<>(getUserFeed);
            } catch(BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
            }
        }

    /**
     * 마이 페이지 조회 API
     * [GET] /users/myPage/{userIdx}
     * @return BaseResponse<GetMyFeedRes>
     */
    @ResponseBody
    @GetMapping("/myPage/{userIdx}") // (GET) 127.0.0.1:9000/users/mypage
    @ApiOperation(value = "마이페이지 정보 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<GetMyFeedRes> getMypage(@PathVariable("userIdx")int userIdx) {
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetMyFeedRes getMyFeedRes = userProvider.getMyFeed(userIdx);
            return new BaseResponse<>(getMyFeedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카카오 회원가입 API
     * [POST] /users/signin/{access-token}
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("/signin/{access-token}") // (POST) 127.0.0.1:9000/users/signin/dsfdsbfuewhiuwf...
    public BaseResponse<PostUserRes> createKakaoUser(@PathVariable("access-token") String token, @RequestBody PostUserReq postUserReq){
        try{
            String email=userService.getKakaoInfo(token);
            //email validation 처리
            if(email.length()==0){
               return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            // 이메일 정규표현
           if(!isRegexEmail(email)){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            PostUserRes postUserRes = userService.createUser(postUserReq,email);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인(가입 여부 확인) API
     * status 확인 필요
     * [POST] /users/login/{kakao-email}
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PatchMapping("/login/{kakao-email}") // (PATCH) 127.0.0.1:9000/users/login/jdshkf@gmail.com
    public BaseResponse<PostLoginRes> UserLogin(@PathVariable("kakao-email") String email){
        try {
            //email validation 처리
            if(email.length()==0){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            // 이메일 정규표현
            if(!isRegexEmail(email)){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
                PostLoginRes postLoginRes = userService.logIn(email);
                return new BaseResponse<>(postLoginRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 정보 변경 API
     * [PATCH] /users/modiUserInfo
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/modiUserInfo") // (PATCH) 127.0.0.1:9000/users/modiUserInfo
    @ApiOperation(value = "회원 정보 변경 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> modifyUserInfo(@RequestBody PatchUserReq patchUserReq){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchUserReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserInfo(patchUserReq);

            String result = "회원 정보 수정을 완료했습니다.";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 세션 변경 API
     * [PATCH] /users/modiUserSession
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/modiUserSession") // (PATCH) 127.0.0.1:9000/users/modiUserSession
    @ApiOperation(value = "회원 정보 변경 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> modifyUserSession(@RequestBody PatchSessionReq patchSessionReq){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchSessionReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserSession(patchSessionReq);

            String result = "세션 수정을 완료했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 삭제 API
     * [PATCH] /users/{userIdx}/status
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/delete") // (PATCH) 127.0.0.1:9000/users/2/delete
    @ApiOperation(value = "회원 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx!= userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.deleteUser(userIdx);

            String result = "회원 삭제를 완료했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 팔로우 하기 API
     * [PATCH] /users/follow/
     * @return
     */

    /**
     * 팔로우 취소 API
     *  * status 말고 delete로
     * [PATCH] /users/unfollow/
     * @return
     */
}