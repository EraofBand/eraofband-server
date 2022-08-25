package com.example.demo.src.board;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.*;

import com.example.demo.src.board.model.PatchBoardComReq;
import com.example.demo.src.board.model.PatchBoardReq;
import com.example.demo.src.board.model.PostBoardReq;
import com.example.demo.src.board.model.PostBoardRes;

import com.example.demo.src.chat.model.GetChatRoomExistRes;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private final BoardProvider boardProvider;

    @Autowired
    private final BoardService boardService;

    @Autowired
    private final JwtService jwtService;

    public BoardController(BoardProvider boardProvider, BoardService boardService, JwtService jwtService){
        this.boardProvider = boardProvider;
        this.boardService = boardService;
        this.jwtService = jwtService;
    }

    /**
     * 게시물 리스트 조회 API
     * [GET] /board/list/info/1/12
     * @return BaseResponse<List<GetBoardRes>>
     */
    @ResponseBody
    @GetMapping("/list/info/{category}/{boardIdx}")   // (get) https://eraofband.shop/board/list/info/1/12
    @ApiOperation(value = "게시물 리스트 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name="category", value="게시 유형 인덱스", required = true),
            @ApiImplicitParam(name="boardIdx", value="현재 조회중인 게시글 인덱스(기준으로 아래 20개 불러오기, 초기값 0)", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetBoardRes>> getBoardList(@PathVariable("category") int category, @PathVariable("boardIdx")int boardIdx) {

        try {

            List<GetBoardRes> getBoardList = boardProvider.retrieveBoard(category, boardIdx);
            return new BaseResponse<>(getBoardList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 게시물 조회 API
     * [GET] /board/info/12
     * @return BaseResponse<GetBoardInfoRes>
     */
    @ResponseBody
    @GetMapping("/info/{boardIdx}")   // (get) https://eraofband.shop/board/info/1
    @ApiOperation(value = "게시물 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="boardIdx", value="게시물 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2104, message="게시글 조회 수 증가에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetBoardInfoRes> getBoardInfo(@PathVariable("boardIdx") int boardIdx){

        try{
            boardService.addViewCount(boardIdx);
            int userIdxByJwt = jwtService.getUserIdx();

            GetBoardInfoRes getBoardInfo=boardProvider.retrieveBoardInfo(userIdxByJwt, boardIdx);
            return new BaseResponse<>(getBoardInfo);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 게시판 게시물 생성 API
     * [POST] /board
     * @return BaseResponse<PostBoardRes>
     */
    @ResponseBody
    @PostMapping("") // (post) https://eraofband.shop/board
    @ApiOperation(value = "게시판 게시물 생성", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값), \n" +
            "이미지 없을 시 공백 처리 imgUrl : '''',\n" +
            "이미지 여러개일 시 {}추가 (사이에 , 도 추가)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2062, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=2101, message="게시글 제목을 입력해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostBoardRes> createBoard(@RequestBody PostBoardReq postBoardReq) {

        if(postBoardReq.getContent().length()>1000){
            return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
        }

        if(postBoardReq.getTitle() == null || postBoardReq.getTitle() == ""){
            return new BaseResponse<>(POST_BOARD_EMPTY_TITLE);
        }



        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(postBoardReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }


            PostBoardRes postBoardRes = boardService.createBoard(userIdxByJwt,postBoardReq);
            return new BaseResponse<>(postBoardRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    /**
     * 게시판 게시물 수정 API
     * [PATCH] /board/board-info/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/board-info/{boardIdx}") // (patch) https://eraofband.shop/board/board-info/2
    @ApiOperation(value = "게시판 게시물 수정", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="boardIdx", value="수정할 게시물 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2062, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2101, message="게시글 제목을 입력해주세요."),
            @ApiResponse(code=2102, message="게시글 수정에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> modifyBoard(@PathVariable("boardIdx") int boardIdx, @RequestBody PatchBoardReq patchBoardReq){
        try{

            if(patchBoardReq.getContent().length()>1000)
            {
                return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
            }


            int userIdxByJwt = jwtService.getUserIdx();
            if(patchBoardReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }
            boardService.modifyBoard(userIdxByJwt, boardIdx, patchBoardReq);

            String result = "게시판 게시물 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시판 게시물 삭제 API
     * [PATCH] /board/status/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{boardIdx}") // (patch) https://eraofband.shop/board/status/2
    @ApiOperation(value = "게시판 게시물 삭제", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="boardIdx", value="삭제할 게시물 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2103, message="게시글 삭제에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deleteBoard(@PathVariable("boardIdx") int boardIdx, @RequestBody PatchBoardComReq patchBoardComReq){
        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            if(patchBoardComReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }
            boardService.deleteBoard(userIdxByJwt,boardIdx);

            String result = "게시판 게시물이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시판 게시물 사진 수정 API
     * [POST] /board/board-img/2
     * @return BaseResponse<GetBoardCommentRes>
     */
    @ResponseBody
    @PostMapping("/board-img/{boardIdx}") // (post) https://eraofband.shop/board/board-img/2
    @ApiOperation(value = "게시판 게시물 사진 수정")
    @ApiImplicitParam(name="boardIdx", value="사진 수정할 게시물 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2109, message="게시글 사진 수정에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> modifyBoardImg(@PathVariable("boardIdx") int boardIdx, @RequestBody PostImgsUrlReq postImgsUrlReq){
        try{

            boardService.modifyBoardImg(boardIdx, postImgsUrlReq);

            String result = "게시물 사진 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시판 게시물 사진 삭제 API
     * [PATCH] /board/status-img/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status-img/{boardImgIdx}") // (patch) https://eraofband.shop/board/status-img/2
    @ApiOperation(value = "게시판 게시물 사진 삭제")
    @ApiImplicitParam(name="boardImgIdx", value="삭제할 사진 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2110, message="게시글 사진 삭제에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deleteBoardImg(@PathVariable("boardImgIdx") int boardImgIdx){
        try {
            boardService.deleteBoardImg(boardImgIdx);

            String result = "게시물 사진이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    /**
     * 게시물 댓글 등록 API
     * [POST] /board/comment/2
     * @return BaseResponse<GetBoardCommentRes>
     */
    @ResponseBody
    @PostMapping("/comment/{boardIdx}") // (post) https://eraofband.shop/board/comment/2
    @ApiOperation(value = "게시물 댓글 등록", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="boardIdx", value="댓글 달 게시물 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2062, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetBoardCommentRes> createComment(@PathVariable("boardIdx") int boardIdx, @RequestBody PostBoardCommentReq postBoardCommentReq) throws IOException {

        if(postBoardCommentReq.getContent().length()>100){
            return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(postBoardCommentReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }

            int boardCommentIdx = boardService.createComment(boardIdx, userIdxByJwt, postBoardCommentReq);
            //원 댓글 그룹 추가
            boardService.addGroupNum(boardCommentIdx);
            //생성한 댓글 조회
            GetBoardCommentRes getComment = boardProvider.certainComment(boardCommentIdx);
            //boardService.sendMessageTo(
            //        "게시물 댓글",
            //        "에 댓글을 남기셨습니다.");
            return new BaseResponse<>(getComment);


        } catch(BaseException exception){
            System.out.println(exception);
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    /**
     * 게시물 대댓글 등록 API
     * [POST] /board/re-comment/2
     * @return BaseResponse<GetBoardCommentRes>
     */
    @ResponseBody
    @PostMapping("/re-comment/{boardIdx}") // (post) https://eraofband.shop/board/re-comment/2
    @ApiOperation(value = "게시물 대댓글 등록", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="boardIdx", value="대댓글 달 게시물 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2105, message="게시글 댓글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2062, message="내용의 글자수를 확인해주세요."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetBoardCommentRes> createReComment(@PathVariable("boardIdx") int boardIdx, @RequestBody PostBoardReCommentReq postBoardReCommentReq) throws IOException {

        if(postBoardReCommentReq.getContent().length()>100){
            return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(postBoardReCommentReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }

            int boardCommentIdx = boardService.createReComment(boardIdx, userIdxByJwt, postBoardReCommentReq);

            //생성한 댓글 조회
            GetBoardCommentRes getComment = boardProvider.certainComment(boardCommentIdx);
//            boardService.sendReMessageTo(
//                    "게시물 답글",
//                    "회원님의 댓글에 답글을 달았습니다.", postBoardCommentReq);
            return new BaseResponse<>(getComment);


        } catch(BaseException exception){
            System.out.println(exception);
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    /**
     * 게시물 댓글 삭제 API
     * [PATCH] /board/comment/status/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/comment/status/{boardCommentIdx}") // (patch) https://eraofband.shop/board/comment/status/2
    @ApiOperation(value = "게시글 댓글 삭제", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="boardCommentIdx", value="삭제할 댓글 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2105, message="게시글 댓글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2106, message="게시글 댓글 삭제에 실패했습니다."),
            @ApiResponse(code=2107, message="답글이 있는 댓글은 삭제할 수 없습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deleteComment(@PathVariable("boardCommentIdx") int boardCommentIdx, @RequestBody PatchBoardComReq patchBoardComReq) {

        try {

            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            if(patchBoardComReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }
            boardService.deleteComment(userIdxByJwt,boardCommentIdx);

            String result = "게시글 댓글이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 게시물 좋아요 API
     * [POST] /board/likes/{boardIdx}
     * @return BaseResponse<PostBoardLikeRes>
     */
    @ResponseBody
    @PostMapping("/likes/{boardIdx}") // (post) https://eraofband.shop/board/likes/2
    @ApiOperation(value = "게시물 좋아요 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="boardIdx", value="좋아요할 게시물 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2108, message="이미 추천한 게시글입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<PostBoardLikeRes> likesBoard(@PathVariable("boardIdx") int boardIdx){

        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            PostBoardLikeRes postBoardLikeRes = boardService.likesBoard(userIdxByJwt, boardIdx);
            return new BaseResponse<>(postBoardLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 작성 게시물 리스트 조회 API
     * [GET] /board/my
     * @return BaseResponse<List<GetMyBoardRes>>
     */
    @ResponseBody
    @GetMapping("/my")   // (get) https://eraofband.shop/board/my
    @ApiOperation(value = "작성 게시물 리스트 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetMyBoardRes>> getMyBoardList(){
        try{
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            List<GetMyBoardRes> getMyBoardList = boardProvider.retrieveMyBoard(userIdxByJwt);
            return new BaseResponse<>(getMyBoardList);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 댓글 단 게시물 리스트 조회 API
     * [GET] /board/my-comment
     * @return BaseResponse<List<GetBoardRes>>
     */
    @ResponseBody
    @GetMapping("/my-comment")   // (get) https://eraofband.shop/board/my-comment
    @ApiOperation(value = "댓글 단 게시물 리스트 조회", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetMyBoardRes>> getMyCommentList() {
        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            List<GetMyBoardRes> getMyCommentBoardList = boardProvider.retrieveMyCommentBoard(userIdxByJwt);
            return new BaseResponse<>(getMyCommentBoardList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 게시물 좋아요 취소 API
     *  * status 말고 delete로
     * [DELETE] /board/unlikes/2
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping ("/unlikes/{boardIdx}") // (delete) https://eraofband.shop/board/unlikes/2
    @ApiOperation(value = "게시물 좋아요 취소 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
    @ApiImplicitParam(name="boardIdx", value="좋아요 취소할 게시물 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=2001, message="JWT를 입력해주세요."),
            @ApiResponse(code=2002, message="유효하지 않은 JWT입니다."),
            @ApiResponse(code=2010, message="유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=2100, message="게시글 아이디 값을 확인해주세요."),
            @ApiResponse(code=2111, message="게시물 좋아요 취소에 실패했습니다."),
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> unlikesBoard(@PathVariable("boardIdx") int boardIdx){

        try {
            //jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();
            boardService.unlikesBoard(userIdxByJwt,boardIdx);

            String result = "게시물 좋아요 취소를 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));

        }

    }
}
