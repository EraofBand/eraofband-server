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
                "        INSERT INTO Band(userIdx, content)\n" +
                "        VALUES (?, ?);";
        Object[] insertBandParams = new Object[]{userIdx, postBandReq.getBandContent()};
        this.jdbcTemplate.update(insertBandQuery, insertBandParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }
}
