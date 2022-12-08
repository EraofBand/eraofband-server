package com.example.demo.src.user;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.notice.model.PostReportReq;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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

//    /**
//     * 카카오 로그인(가입 여부 확인) API
//     * [POST] /users/login/{access-token}
//     * @return BaseResponse<PostLoginRes>
//     */
//    @ResponseBody
//    @PostMapping("/login/{access-token}") // (POST) eraofband.sop/users/login/dsfdsbfuewhiuwf...
//    @ApiImplicitParam(name="access-token", value="접근 가능 토큰", required = true)
//    @ApiResponses({
//            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
//            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
//            @ApiResponse(code=2015, message="이메일을 입력해주세요."),
//            @ApiResponse(code=2016, message="이메일 형식을 확인해주세요."),
//            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
//    })
//    public BaseResponse<PostLoginRes> UserLogin(@PathVariable("access-token") String token){
//        try {
//            String email=userService.getKakaoInfo(token);
//            //이메일 필수 처리
//            if(email.length()==0){
//                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
//            }
//            // 이메일 정규표현
//            if(!isRegexEmail(email)){
//                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
//            }
//            PostLoginRes postLoginRes = userService.logIn(email);
//            return new BaseResponse<>(postLoginRes);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 카카오 로그인(가입 여부 확인) API
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
     * 자동 로그인 api
     * [PATCH] /users/auto-login/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/auto-login/{userIdx}") // (PATCH) eraofband.sop/users/auto-login/12
    @ApiOperation(value = "자동 로그인 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2007, message="유저가 로그아웃 상태입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> autoLogin(@PathVariable("userIdx") int userIdx){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //로그인 상태인지 확인
            int log=userProvider.checkLogin(userIdx);
            if(log==0){
                return new BaseResponse<>(INVALID_USER_STATUS);
            }

            String result="자동 로그인 가능합니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * refresh token으로 jwt 재발급 api
     * [PATCH] /users/refresh/{userIdx}
     * @return BaseResponse<PatchLoginRes>
     */
    @ResponseBody
    @PatchMapping("/refresh/{userIdx}") // (PATCH) eraofband.sop/users/refresh
    @ApiOperation(value = "jwt 재발급 처리", notes = "헤더에 refresh token 필요(key: X-ACCESS-TOKEN, value: refresh token 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2008, message="유효 시간이 지나 로그아웃 처리되었습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PatchLoginRes> reJwt(@PathVariable("userIdx") int userIdx){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //refresh token 유효시간 검사
            int expTime=jwtService.getRefExp();
            Date current=new Date(System.currentTimeMillis());
            int cur=(int)current.getTime();
            if(cur>expTime){
                //로그아웃 처리
                userService.logoutUser(userIdx);
                return  new BaseResponse<>(PATCH_USER_STATUS);
            }

            //refresh token 값 db와 비교
            String ref=jwtService.getJwt();
            int check=userProvider.checkUserRef(userIdx,ref);
            if(check==0){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //jwt 유효시간
            Date time=new Date(System.currentTimeMillis()+1*(1000*60*60*24*365));
            int exp= (int) time.getTime();
            //새 jwt 발급
            String jwt = jwtService.createJwt(userIdx);
            PatchLoginRes patchLoginRes = new PatchLoginRes(jwt, exp);

            return new BaseResponse<>(patchLoginRes);
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
     * 회원 로그아웃 API
     * [PATCH] /users/logout/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/logout/{userIdx}") // (PATCH) eraofband.shop/users/logout/2
    @ApiOperation(value = "회원 로그아웃 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="로그아웃할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code=4017, message="회원 로그아웃에 실패하였습니다.")
    })
    public BaseResponse<String> logoutUser(@PathVariable("userIdx") int userIdx){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx!= userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.logoutUser(userIdx);

            String result = "회원 로그아웃을 완료했습니다.";
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

            //상대가 로그아웃 상태인지 확인
            int log=userProvider.checkLogin(userIdx);
            if(log==1) {
                //userService.sendMessageTo(
                //        userIdxByJwt,
                //        userIdx,
                //        "팔로우",
                //        "님이 회원님을 팔로우 했습니다.");
            }

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

    /**
     * 차단 API
     * [POST] /users/block/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/block/{userIdx}") // (post) https://eraofband.shop//user/block/2
    @ApiOperation(value = "차단 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="차단할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> insertBlock(@PathVariable("userIdx") int userIdx) {
        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            userService.insertBlock(userIdxByJwt, userIdx);

            String result = "차단을 완료했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 차단 해제 API
     * [DELETE] /users/unblock/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping ("/unblock/{userIdx}") // (delete) eraofband.shop/users/unblock/2
    @ApiOperation(value = "차단 해제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="userIdx", value="차단 해제할 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2007, message="유저 차단 해제에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> unBlock(@PathVariable("userIdx") int userIdx){
        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            userService.unBlock(userIdxByJwt,userIdx);
            String result = "해당 유저의 차단을 해제하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 차단 목록 조회 API
     * [GET] /users/info/block
     * @return BaseResponse<List<GetBlockRes>>
     */
    @ResponseBody
    @GetMapping("/info/block") // (GET) eraofband.shop/users/info/block
    @ApiOperation(value = "차단 목록 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetBlockRes>> getBlock() {
        try{
            int userIdxByJwt = jwtService.getUserIdx();

            List<GetBlockRes> getBlockRes = userProvider.getBlock(userIdxByJwt);
            return new BaseResponse<>(getBlockRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}