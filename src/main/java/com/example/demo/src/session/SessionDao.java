package com.example.demo.src.session;

import com.example.demo.src.pofol.model.PatchPofolReq;
import com.example.demo.src.session.model.PatchBandReq;
import com.example.demo.src.session.model.PostBandReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SessionDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 유저 확인
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                                                int.class,
                                                checkUserExistParams);

    }

    // 밴드 확인
    public int checkBandExist(int bandIdx){
        String checkBandExistQuery = "select exists(select bandIdx from Band where bandIdx = ?)";
        int checkBandExistParams = bandIdx;
        return this.jdbcTemplate.queryForObject(checkBandExistQuery,
                                                int.class,
                                                checkBandExistParams);

    }

    // 밴드 생성
    public int insertBand(int userIdx, PostBandReq postBandReq){
        String insertBandQuery = "INSERT INTO Band(userIdx, bandTitle, bandIntroduction, bandRegion, bandContent, vocal, guitar, base, keyboard, drum, bandImgUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertBandParams = new Object[]{ userIdx, postBandReq.getBandTitle(), postBandReq.getBandIntroduction(),
                postBandReq.getBandRegion(), postBandReq.getBandContent(), postBandReq.getVocal(), postBandReq.getGuitar(),
                postBandReq.getBase(), postBandReq.getKeyboard(), postBandReq.getDrum(), postBandReq.getBandImgUrl()
        };
        this.jdbcTemplate.update(insertBandQuery, insertBandParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }

    // 밴드 수정
    public int updateBand(int bandIdx, PatchBandReq patchBandReq){
        String updateBandQuery = "UPDATE Band SET bandContent = ? WHERE bandIdx = ?" ;
        Object[] updateBandParams = new Object[]{patchBandReq.getBandContent(), bandIdx};

        return this.jdbcTemplate.update(updateBandQuery,updateBandParams);
    }

    // 밴드 삭제
    public int updateBandStatus(int bandIdx){
        String deleteBandQuery = "UPDATE Band SET status = 'INACTIVE' WHERE bandIdx = ? ";
        Object[] deleteBandParams = new Object[]{bandIdx};

        return this.jdbcTemplate.update(deleteBandQuery,deleteBandParams);
    }
}
