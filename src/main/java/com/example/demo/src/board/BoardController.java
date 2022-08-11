package com.example.demo.src.board;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.*;

import com.example.demo.src.board.model.PatchBoardComReq;
import com.example.demo.src.board.model.PatchBoardReq;
import com.example.demo.src.board.model.PostBoardReq;
import com.example.demo.src.board.model.PostBoardRes;

import com.example.demo.src.pofol.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import com.example.demo.src.pofol.PofolProvider;
import com.example.demo.src.pofol.PofolService;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
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
     * [GET] /board/list/info/12
     * @return BaseResponse<List<GetBoardRes>>
     */
    @ResponseBody
    @GetMapping("/list/info/{category}")   // (get) https://eraofband.shop/board/list/info/1
    @ApiOperation(value = "게시물 리스트 조회")
    @ApiImplicitParam(name="category", value="게시 유형 인덱스", required = true)
    @ApiResponses({
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<GetBoardRes>> getBoardList(@PathVariable("category") int category){

        try{

            List<GetBoardRes> getBoardList=boardProvider.retrieveBoard(category);
            return new BaseResponse<>(getBoardList);
        } catch (BaseException exception){
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
    @ApiOperation(value = "게시판 게시물 생성 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
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
    @ApiOperation(value = "게시판 게시물 수정 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
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
    @ApiOperation(value = "게시판 게시물 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
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
     * 게시물 댓글 등록 API
     * [POST] /board/comment/2
     * @return BaseResponse<GetBoardCommentRes>
     */
    @ResponseBody
    @PostMapping("/comment/{boardIdx}") // (post) https://eraofband.shop/board/comment/2
    @ApiOperation(value = "게시물 댓글 등록 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
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

            int boardCommentIdx = boardService.createComment(boardIdx, userIdxByJwt,postBoardCommentReq);
            //댓글 그룹 추가
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
    @ApiOperation(value = "게시물 대댓글 등록 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
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
    public BaseResponse<GetBoardCommentRes> createReComment(@PathVariable("boardIdx") int boardIdx, @RequestBody PostBoardCommentReq postBoardCommentReq) throws IOException {

        if(postBoardCommentReq.getContent().length()>100){
            return new BaseResponse<>(POST_POSTS_INVALID_CONTENTS);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(postBoardCommentReq.getUserIdx()!= userIdxByJwt){
                return new BaseResponse<>(INVALID_JWT);
            }

            int boardCommentIdx = boardService.createReComment(boardIdx, userIdxByJwt,postBoardCommentReq);

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
    @ApiOperation(value = "게시글 댓글 삭제 처리", notes = "헤더에 jwt 필요(key: X-ACCESS-TOKEN, value: jwt 값)")
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
     * 게시물 추천 API
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
}
