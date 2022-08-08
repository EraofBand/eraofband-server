package com.example.demo.src.pofol;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.pofol.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.POST_POSTS_INVALID_CONTENTS;
import static org.hibernate.sql.InFragment.NULL;
// import static com.example.demo.utils.ValidationRegex.isRegexNickName;


@RestController
@RequestMapping("/pofols")
public class PofolController {

    @Autowired
    private final PofolProvider pofolProvider;

    @Autowired
    private final PofolService pofolService;

    @Autowired
    private final JwtService jwtService;

    public PofolController(PofolProvider pofolProvider, PofolService pofolService, JwtService jwtService){
        this.pofolProvider = pofolProvider;
        this.pofolService = pofolService;
        this.jwtService = jwtService;
    }


    /**
     * 전체 포트폴리오 리스트 조회 API
     * [GET] /pofols/info/all/0
     * 유저 인덱스 검색 포트폴리오 리스트 조회 API
     * @return BaseResponse<List<GetPofolRes>>
     */
    @ResponseBody
    @GetMapping("/info/all/{pofolIdx}")  // (get) https://eraofband.shop/pofols/info/all/0
    @ApiOperation(value = "전체 포트폴리오 리스트 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="pofolIdx", value="현재 조회중인 포폴 인덱스(기준으로 아래 20개 불러오기, 초기값 0)", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetPofolRes>> getPofol(@PathVariable("pofolIdx") int pofolIdx){
        try{

            int userIdxByJwt = jwtService.getUserIdx();

            List<GetPofolRes> getPofol=pofolProvider.retrieveAllPofol(userIdxByJwt, pofolIdx);
            return new BaseResponse<>(getPofol);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 팔로우 한 유저 포트폴리오 리스트 조회 API
     * [GET] /pofols/info/follow/12/0
     * 유저 인덱스 검색 포트폴리오 리스트 조회 API
     * @return BaseResponse<List<GetPofolRes>>
     */
    @ResponseBody
    @GetMapping("/info/follow/{userIdx}/{pofolIdx}")  // (get) https://eraofband.shop/pofols/info/follow/12/0
    @ApiOperation(value = " 팔로우 한 유저 포트폴리오 리스트 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParams({@ApiImplicitParam(name="userIdx", value="유저 인덱스", required = true),
            @ApiImplicitParam(name="pofolIdx", value="현재 조회중인 포폴 인덱스(기준으로 아래 20개 불러오기, 초기값 0)", required = true)})
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetPofolRes>> getFollowPofol(@PathVariable("userIdx") int userIdx, @PathVariable("pofolIdx") int pofolIdx){
        try{

            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }

            List<GetPofolRes> getPofol=pofolProvider.retrievePofol(userIdx, pofolIdx);
            return new BaseResponse<>(getPofol);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 포트폴리오 리스트 조회 API
     * [GET] /pofols/info/12
     * 유저 인덱스 검색 포트폴리오 조회 API
     * @return BaseResponse<List<GetPofolRes>>
     */
    @ResponseBody
    @GetMapping("/info/{userIdx}")   // (get) https://eraofband.shop/pofols/info/12
    @ApiOperation(value = "포트폴리오 리스트 조회")
    @ApiImplicitParam(name="userIdx", value="해당 유저 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetPofolRes>> getMyPofol(@PathVariable("userIdx") int userIdx){

        try{

            List<GetPofolRes> getMyPofol=pofolProvider.retrieveMyPofol(userIdx);
            return new BaseResponse<>(getMyPofol);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 포트폴리오 생성 API
     * [POST] /pofol
     * @return BaseResponse<PostPofolRes>
     */
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/pofol
    @ApiOperation(value = "포트폴리오 생성 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2062, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=2063, message="포트폴리오 동영상을 입력해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostPofolRes> createPofol(@RequestBody PostPofolReq postPofolReq) {

        if(postPofolReq.getContent().length()>450){
            return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
        }
        if(postPofolReq.getVideoUrl() == null){
           return new BaseResponse<>(POST_POSTS_EMPTY_VIDEOURL);
        }


        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(postPofolReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }

            PostPofolRes postPofolRes = pofolService.createPofol(userIdxByJwt,postPofolReq);
            return new BaseResponse<>(postPofolRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 포트폴리오 수정 API
     * [PATCH] /pofol/pofol-info/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/pofol-info/{pofolIdx}/") // (patch) https://eraofband.shop/pofol/pofol-info/2
    @ApiOperation(value = "포트폴리오 수정 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="pofolIdx", value="수정할 포트폴리오 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2060, message="포트폴리오 아이디 값을 확인해주세요."),
            @ApiResponse(code=2062, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=2064, message="포트폴리오 수정에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> modifyPofol(@PathVariable("pofolIdx") int pofolIdx, @RequestBody PatchPofolReq patchPofolReq){
        try{

            if(patchPofolReq.getContent().length()>450)
            {
                return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
            }


            int userIdxByJwt = jwtService.getUserIdx();
            if(patchPofolReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }
            pofolService.modifyPofol(userIdxByJwt, pofolIdx, patchPofolReq);

            String result = "포트폴리오 내용 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 포트폴리오 삭제 API
     * [PATCH] /pofol/status/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{pofolIdx}") // (patch) https://eraofband.shop/pofol/status/2
    @ApiOperation(value = "포트폴리오 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="pofolIdx", value="삭제할 포트폴리오 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2060, message="포트폴리오 아이디 값을 확인해주세요."),
            @ApiResponse(code=2062, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=2065, message="포트폴리오 삭제에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deletePofol(@PathVariable("pofolIdx") int pofolIdx, @RequestBody PatchPofComReq patchPofComReq){
        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            if(patchPofComReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }
            pofolService.deletePofol(userIdxByJwt,pofolIdx);

            String result = "포트폴리오가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 포트폴리오 좋아요 API
     * [POST] /pofol/likes/2
     * @return BaseResponse<PostLikeRes>
     */
    @ResponseBody
    @PostMapping("/likes/{pofolIdx}") // (post) https://eraofband.shop/pofol/likes/2
    @ApiOperation(value = "포트폴리오 좋아요 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="pofolIdx", value="좋아요할 포트폴리오 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2060, message="포트폴리오 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostLikeRes> likesPofol(@PathVariable("pofolIdx") int pofolIdx){
//
        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();


            PostLikeRes postlLikeRes = pofolService.likesPofol(userIdxByJwt,pofolIdx);
            return new BaseResponse<>(postlLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 포트폴리오 좋아요 취소 API
     *  * status 말고 delete로
     * [DELETE] /pofol/unlikes/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping ("/unlikes/{pofolIdx}") // (delete) https://eraofband.shop/pofol/unlikes/2
    @ApiOperation(value = "포트폴리오 좋아요 취소 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="pofolIdx", value="좋아요 취소할 포트폴리오 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2060, message="포트폴리오 아이디 값을 확인해주세요."),
            @ApiResponse(code=2068, message="포트폴리오 좋아요 취소에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> unlikesPofol(@PathVariable("pofolIdx") int pofolIdx){

        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            pofolService.unlikesPofol(userIdxByJwt,pofolIdx);

            String result = "포트폴리오 좋아요 취소를 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 포트폴리오 댓글 등록 API
     * [POST] /pofol/comment/2
     * @return BaseResponse<GetCommentRes>
     */
    @ResponseBody
    @PostMapping("/comment/{pofolIdx}") // (post) https://eraofband.shop/pofol/comment/2
    @ApiOperation(value = "포트폴리오 댓글 등록 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="pofolIdx", value="댓글 달 포트폴리오 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2060, message="포트폴리오 아이디 값을 확인해주세요."),
            @ApiResponse(code=2062, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetCommentRes> createComment(@PathVariable("pofolIdx") int pofolIdx, @RequestBody PostCommentReq postCommentReq) {

        if(postCommentReq.getContent().length()>100){
            return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(postCommentReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }

            int pofolCommentIdx = pofolService.createComment(pofolIdx, userIdxByJwt,postCommentReq);

            GetCommentRes getComment = pofolProvider.certainComment(pofolCommentIdx);
            return new BaseResponse<>(getComment);


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 포트폴리오 댓글 삭제 API
     * [PATCH] /pofol/comment/status/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/comment/status/{pofolCommentIdx}") // (patch) https://eraofband.shop/pofol/comment/status/2
    @ApiOperation(value = "포트폴리오 댓글 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="pofolCommentIdx", value="삭제할 댓글 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2061, message="포트폴리오 댓글 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deleteComment(@PathVariable("pofolCommentIdx") int pofolCommentIdx, @RequestBody PatchPofComReq patchPofComReq) {

        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            if(patchPofComReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }
            pofolService.deleteComment(userIdxByJwt,pofolCommentIdx);

            String result = "포트폴리오 댓글이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }



    }

    /**
     * 포트폴리오 댓글 리스트 조회 API
     * [GET] /pofol/info/comment
     * @return BaseResponse<List<GetCommentRes>>
     */
    @ResponseBody
    @GetMapping("/info/comment")  // (get) https://eraofband.shop/pofol/info/comment
    @ApiOperation(value = "포트폴리오 댓글 목록 조회")
    @ApiImplicitParam(name="pofolIdx", value="댓글 조회할 포트폴리오 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2061, message="포트폴리오 댓글 아이디 값을 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetCommentRes>> getListComment(@RequestParam int pofolIdx){
        try{
            //jwt 없애기
            //int userIdxByJwt = jwtService.getUserIdx();
            //List<GetPofolRes> getPofol=pofolProvider.retrievePofol(userIdxByJwt);q

            List<GetCommentRes> getListComment=pofolProvider.retrieveComment(pofolIdx);
            return new BaseResponse<>(getListComment);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
