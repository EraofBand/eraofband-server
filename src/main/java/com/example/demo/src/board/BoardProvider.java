package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.src.board.model.GetBoardCommentRes;
import com.example.demo.src.board.model.GetBoardImgsRes;
import com.example.demo.src.board.model.GetBoardInfoRes;
import com.example.demo.src.board.model.GetBoardRes;
import com.example.demo.src.pofol.PofolDao;
import com.example.demo.src.pofol.model.GetCommentRes;
import com.example.demo.src.pofol.model.GetPofolRes;
import com.example.demo.src.session.model.GetSessionMemRes;
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

    private List<GetBoardImgsRes> getBoardImgsRes;

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
     * 게시글 댓글 확인
     */
    public int checkCommentExist(int boardCommentIdx) throws BaseException{
        try{
            return boardDao.checkCommentExist(boardCommentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 대댓글 여부 확인
     */
    public int checkReplyExist(int boardCommentIdx) throws BaseException{
        try{
            return boardDao.checkReplyExist(boardCommentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 특정 댓글 조회
     */
    public GetBoardCommentRes certainComment (int boardCommentIdx) throws BaseException {

        try{
            GetBoardCommentRes getCommentRes = boardDao.certainComment(boardCommentIdx);
            return getCommentRes;
        } catch(Exception exception){
            System.out.println(exception);
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
        getBoardImgsRes=retrieveBoardImgs(boardIdx);
        if(checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(checkBoardExist(boardIdx) ==0){
            throw new BaseException(POSTS_EMPTY_BOARD_ID);
        }
        try{
            getBoardComments=boardDao.selectComment(boardIdx);
            GetBoardInfoRes getBoardInfo = boardDao.selectBoardInfo(userIdx, boardIdx, getBoardImgsRes, getBoardComments);
            return getBoardInfo;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시물 이미지 리스트 조회
     */
    public List<GetBoardImgsRes> retrieveBoardImgs(int boardIdx) throws BaseException {
        try{
            List<GetBoardImgsRes> getBoardImgList = boardDao.getBoardImgsRes(boardIdx);
            return getBoardImgList;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시물 좋아요 중복 확인
     */
    public int checkBoardLiked(int userIdx, int boardIdx) throws BaseException{
        try{
            return boardDao.checkBoardLiked(userIdx, boardIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
