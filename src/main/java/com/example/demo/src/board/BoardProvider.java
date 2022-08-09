package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.src.board.model.GetBoardCommentRes;
import com.example.demo.src.board.model.GetBoardInfoRes;
import com.example.demo.src.board.model.GetBoardRes;
import com.example.demo.src.pofol.PofolDao;
import com.example.demo.src.pofol.model.GetPofolRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class BoardProvider {

    private final BoardDao boardDao;
    private final JwtService jwtService;

    private List<GetBoardCommentRes> getBoardComments;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public BoardProvider(BoardDao boardDao, JwtService jwtService) {
        this.boardDao = boardDao;
        this.jwtService = jwtService;
    }

    /**
     * 유저 확인
     */
    public int checkUserExist(int userIdx) throws BaseException {
        try{
            return boardDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시판 게시물 확인
     */
    public int checkBoardExist(int boardIdx) throws BaseException{
        try{
            return boardDao.checkBoardExist(boardIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시물 리스트 조회
     */
    public List<GetBoardRes> retrieveBoard(int category) throws BaseException {
        try{
            List<GetBoardRes> getBoardList = boardDao.selectBoardList(category);
            return getBoardList;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시물 조회
     */
    public GetBoardInfoRes retrieveBoardInfo(int userIdx, int boardIdx) throws BaseException {
        if(checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(checkBoardExist(boardIdx) ==0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }
        try{
            getBoardComments=boardDao.selectComment(boardIdx);
            GetBoardInfoRes getBoardInfo = boardDao.selectBoardInfo(userIdx, boardIdx, getBoardComments);
            return getBoardInfo;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
