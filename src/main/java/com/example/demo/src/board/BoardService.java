package com.example.demo.src.board;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.PatchBoardReq;
import com.example.demo.src.board.model.PostBoardReq;
import com.example.demo.src.board.model.PostBoardRes;
import com.example.demo.src.pofol.PofolDao;
import com.example.demo.src.pofol.PofolProvider;
import com.example.demo.src.pofol.model.PatchPofolReq;
import com.example.demo.src.pofol.model.PostPofolReq;
import com.example.demo.src.pofol.model.PostPofolRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.MODIFY_FAIL_POFOL;

@Service
public class BoardService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BoardDao boardDao;
    private final BoardProvider boardProvider;
    private final JwtService jwtService;

    private int result;


    @Autowired
    public BoardService(BoardDao boardDao, BoardProvider boardProvider, JwtService jwtService) {
        this.boardDao = boardDao;
        this.boardProvider = boardProvider;
        this.jwtService = jwtService;

    }

    /**
     * 게시판 게시물 생성
     */
    public PostBoardRes createBoard(int userIdx, PostBoardReq postBoardReq) throws BaseException {

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            int boardIdx = boardDao.insertBoard(userIdx, postBoardReq);
            return new PostBoardRes(boardIdx);
        } catch (Exception exception) {

            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시판 게시물 수정
     */
    public void modifyBoard(int userIdx, int boardIdx, PatchBoardReq patchBoardReq) throws BaseException {

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(boardProvider.checkBoardExist(boardIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }

        if(patchBoardReq.getTitle() == null || patchBoardReq.getTitle() == ""){
            throw new BaseException(POST_BOARD_EMPTY_TITLE);
        }

        try{
            result = boardDao.updateBoard(boardIdx,patchBoardReq);
        } catch(Exception exception){

            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(MODIFY_FAIL_BOARD);
        }
    }

    /**
     * 게시판 게시물 삭제
     */
    public void deleteBoard(int userIdx,int boardIdx) throws BaseException {

        if(boardProvider.checkBoardExist(boardIdx) == 0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }

        if(boardProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            result = boardDao.updateBoardStatus(boardIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(DELETE_FAIL_BOARD);
        }
    }
}
