package com.example.demo.src.board;

import com.example.demo.src.board.model.PatchBoardReq;
import com.example.demo.src.board.model.PostBoardReq;
import com.example.demo.src.pofol.model.PatchPofolReq;
import com.example.demo.src.pofol.model.PostPofolReq;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class BoardDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 유저 확인
     * */
    public int checkUserExist(int userIdx) {
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ? and status = 'ACTIVE')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    /**
     * 게시판 게시물 확인
     * */
    public int checkBoardExist(int boardIdx) {
        String checkPostExistQuery = "select exists(select boardIdx from Board where boardIdx = ? and status = 'ACTIVE')";
        int checkPostExistParams = boardIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    /**
     * 게시판 게시물 생성
     * */
    public int insertBoard(int userIdx, PostBoardReq postBoardReq) {
        String insertBoardQuery = "INSERT INTO Board(userIdx, category, content, imgUrl, title, views) VALUES (?, ?, ?, ?, ?, 0)";
        Object[] insertBoardParams = new Object[]{userIdx, postBoardReq.getCategory(), postBoardReq.getContent(), postBoardReq.getImgUrl(), postBoardReq.getTitle()};
        this.jdbcTemplate.update(insertBoardQuery, insertBoardParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }


    /**
     * 게시판 게시물 수정
     * */
    public int updateBoard(int boardIdx, PatchBoardReq patchBoardReq) {
        String updateBoardQuery = "UPDATE Board SET title = ?, content = ?, imgUrl = ? WHERE boardIdx = ? and status='ACTIVE'\n";
        Object[] updateBoardParams = new Object[]{patchBoardReq.getTitle(), patchBoardReq.getContent(), patchBoardReq.getImgUrl(), boardIdx};

        return this.jdbcTemplate.update(updateBoardQuery, updateBoardParams);
    }

    /**
     * 게시판 게시물 삭제
     * */
    public int updateBoardStatus(int boardIdx) {
        String deleteBoardQuery = "update Board b" +
                "    left join BoardComment as bc on (bc.boardIdx=b.boardIdx)\n" +
                "    left join BoardLike as bl on (bl.boardIdx=b.boardIdx)\n" +
                "        set b.status='INACTIVE',\n" +
                "            bc.status='INACTIVE',\n" +
                "            bl.status='INACTIVE'\n" +
                "   where b.boardIdx = ? ";
        Object[] deleteBoardParams = new Object[]{boardIdx};

        return this.jdbcTemplate.update(deleteBoardQuery, deleteBoardParams);
    }

}
