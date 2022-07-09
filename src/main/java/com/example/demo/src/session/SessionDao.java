package com.example.demo.src.session;

import com.example.demo.src.session.model.PostBandReq;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class SessionDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 밴드 생성
    public int insertBand(int userIdx, PostBandReq postBandReq){
        String insertBandQuery =
                "       INSERT INTO Band(userIdx, bandName, bandIntroduction, bandRegion, bandContent, " +
                        "vocal, guitar, base, keyboard, drum, bandImgUrl)\n" +
                "        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] insertBandParams = new Object[]{ userIdx, postBandReq.getBandName(),postBandReq.getBandIntroduction(),
                postBandReq.getBandRegion(), postBandReq.getBandContent(), postBandReq.getVocal(), postBandReq.getGuitar(),
                postBandReq.getBase(), postBandReq.getKeyboard(), postBandReq.getDrum(), postBandReq.getBandImgUrl()
        };
        this.jdbcTemplate.update(insertBandQuery, insertBandParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }
}
