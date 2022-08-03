package com.example.demo.src.user;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

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
     * 다른 유저 페이지 조회 API
     * [GET] /users/info/{userIdx}
     * 유저 인덱스 검색 조회 API
     * @return BaseResponse<GetUserFeedRes>
     */
    @ResponseBody
    @GetMapping("/info/{userIdx}") // (GET) eraofband.shop/users/info/2
    @ApiOperation(value = "다른 회원 정보 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="조회할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetUserFeedRes> getUserByIdx(@PathVariable("userIdx")int userIdx) {
            try{
                //jwt에서 idx 추출
                int myId = jwtService.getUserIdx();
                GetUserFeedRes getUserFeed = userProvider.getUserByIdx(myId,userIdx);
                return new BaseResponse<>(getUserFeed);
            } catch(BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
            }
        }

    /**
     * 마이 페이지 조회 API
     * [GET] /users/info/my-page/{userIdx}
     * @return BaseResponse<GetMyFeedRes>
     */
    @ResponseBody
    @GetMapping("/info/my-page/{userIdx}") // (GET) eraofband.shop/users/info/my-page/12
    @ApiOperation(value = "마이페이지 정보 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="조회할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetMyFeedRes> getMypage(@PathVariable("userIdx")int userIdx) {
        try{
            //jwt에서 idx 추출
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
    @PostMapping("/signin/{access-token}") // (POST) eraofband.shop/users/signin/dsfdsbfuewhiuwf...
    @ApiImplicitParam(name="access-token", value="접근 가능 토큰", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2015, message="이메일을 입력해주세요."),
            @ApiResponse(code=2016, message="이메일 형식을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostUserRes> createKakaoUser(@PathVariable("access-token") String token, @RequestBody PostUserReq postUserReq){
        try{
            String email=userService.getKakaoInfo(token);
            //이메일 필수 처리
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
    @PostMapping("/login/{kakao-email}") // (POST) eraofband.sop/users/login/jdshkf@gmail.com
    @ApiImplicitParam(name="kakao-email", value="회원가입용 이메일", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2015, message="이메일을 입력해주세요."),
            @ApiResponse(code=2016, message="이메일 형식을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostLoginRes> UserLogin(@PathVariable("kakao-email") String email){
        try {
            //이메일 필수 처리
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
    @PatchMapping("/user-info") // (PATCH) eraofband.shop/users/user-info
    @ApiOperation(value = "회원 정보 변경 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code=4014, message="회원 정보 수정에 실패하였습니다.")
    })
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
    @PatchMapping("/user-session") // (PATCH) eraofband.shop/users/user-session
    @ApiOperation(value = "회원 세션 변경 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code=4015, message="회원 정보 수정에 실패하였습니다.")
    })
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
     * [PATCH] /users/status/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{userIdx}") // (PATCH) eraofband.shop/users/status/2
    @ApiOperation(value = "회원 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="삭제할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code=4016, message="회원 삭제에 실패하였습니다.")
    })
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
     * [POST] /users/follow/{userIdx}
     * @return BaseResponse<PostFollowRes>
     */
    @ResponseBody
    @PostMapping("/follow/{userIdx}") // (post) eraofband.shop/users/follow/10
    @ApiOperation(value = "팔로우 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="팔로우할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2070, message="유저 팔로우에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostFollowRes> followUser(@PathVariable("userIdx") int userIdx) throws IOException {
        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            PostFollowRes postFollowRes = userService.followUser(userIdxByJwt,userIdx);
//            userService.sendMessageTo(
//                    userIdxByJwt,
//                    userIdx,
//                    "팔로우",
//                    "님이 회원님을 팔로우 했습니다.");

            return new BaseResponse<>(postFollowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 팔로우 취소 API
     * delete로
     * [DELETE] /users/unfollow/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping ("/unfollow/{userIdx}") // (delete) eraofband.shop/users/unfollow/2
    @ApiOperation(value = "팔로우 취소 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="언팔로우할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2071, message="유저 팔로우 취소에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
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
     * [GET] /users/info/follow/{userIdx}
     * @return BaseResponse<GetFollowRes>
     */
    @ResponseBody
    @GetMapping("/info/follow/{userIdx}") // (GET) eraofband.shop/users/info/follow/12
    @ApiOperation(value = "팔로잉, 팔로워 리스트 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="조회할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetFollowRes> getFollow(@PathVariable("userIdx")int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();

            GetFollowRes getFollowRes = userProvider.getFollow(userIdxByJwt, userIdx);
            return new BaseResponse<>(getFollowRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}