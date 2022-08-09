package com.example.demo.src.board;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.*;
import com.example.demo.src.pofol.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/list/info/{category}")   // (get) https://eraofband.shop/board/info/1
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
            @ApiResponse(code=4000, message="데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<GetBoardInfoRes> getBoardInfo(@PathVariable("boardIdx") int boardIdx){

        try{
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
            @ApiResponse(code=2102, message="게시글 삭제에 실패했습니다."),
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
}
