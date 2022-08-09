package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.src.pofol.PofolDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class BoardProvider {

    private final BoardDao boardDao;
    private final JwtService jwtService;


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

}
