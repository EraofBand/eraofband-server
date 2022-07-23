package com.example.demo.src.user;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.pofol.model.PostLikeRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
     * 다른 회원 페이지 조회 API
     * [GET] /users/{userIdx}
     * 유저 인덱스 검색 조회 API
     * @return BaseResponse<GetUserFeedRes>
     */
    @ResponseBody
    @GetMapping("/info/{userIdx}") // (GET) 127.0.0.1:9000/users/info/2
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
     * [GET] /users/my-page/{userIdx}
     * @return BaseResponse<GetMyFeedRes>
     */
    @ResponseBody
    @GetMapping("/info/my-page/{userIdx}") // (GET) 127.0.0.1:9000/users/info/mypage/12
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
    @ApiImplicitParam(name="access-token", value="접근 가능 토큰", required = true)
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
     * [POST] /users/login/{kakao-email}
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login/{kakao-email}") // (POST) 127.0.0.1:9000/users/login/jdshkf@gmail.com
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
     * [PATCH] /users/user-info
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/user-info") // (PATCH) 127.0.0.1:9000/users/user-info
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
     * [PATCH] /users/user-session
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/user-session") // (PATCH) 127.0.0.1:9000/users/modiUserSession
    @ApiOperation(value = "회원 세션 변경 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
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
     * [PATCH] /users/delete/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{userIdx}") // (PATCH) 127.0.0.1:9000/users/2/delete
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
     * [POST] /users/follow/10
     * @return BaseResponse<PostFollowRes>
     */
    @ResponseBody
    @PostMapping("/follow/{userIdx}") // (post) https://eraofband.shop/users/follow/10
    @ApiOperation(value = "팔로우 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<PostFollowRes> followUser(@PathVariable("userIdx") int userIdx){
//
        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            PostFollowRes postFollowRes = userService.followUser(userIdxByJwt,userIdx);
            return new BaseResponse<>(postFollowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 팔로우 취소 API
     *  * status 말고 delete로
     * [DELETE] /users/unfollow/10
     * @return BaseResponse<String>
     */
    // 포트폴리오 좋아요 취소
    @ResponseBody
    @DeleteMapping ("/unfollow/{userIdx}") // (post) https://eraofband.shop/users/unfollow/2
    @ApiOperation(value = "팔로우 취소 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    public BaseResponse<String> unFollowUser(@PathVariable("userIdx") int userIdx){
        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            userService.unfollowUser(userIdxByJwt,userIdx);

            String result = "팔로우 취소를 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 팔로잉, 팔로워 리스트 조회 API
     * [GET] /users/follow-info/10
     * @return BaseResponse<GetFollowRes>
     */
    @ResponseBody
    @GetMapping("/info/follow/{userIdx}") // (GET) 127.0.0.1:9000/users/info/follow/12
    @ApiOperation(value = "팔로잉, 팔로워 리스트 조회")
    public BaseResponse<GetFollowRes> getFollow(@PathVariable("userIdx")int userIdx) {
        try{
            GetFollowRes getFollowRes = userProvider.getFollow(userIdx);
            return new BaseResponse<>(getFollowRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}