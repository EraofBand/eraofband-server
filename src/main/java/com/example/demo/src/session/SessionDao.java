package com.example.demo.src.session;

import com.example.demo.src.session.model.GetBandRes;
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
    public int checkUserIsMe(int bandIdx){
        String checkUserExistQuery = "SELECT userIdx FROM Band WHERE bandIdx = ?";
        int checkUserExistParams = bandIdx;
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

    // 밴드 조회
    public GetBandRes getMyBandByIdx(int bandIdx){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, b.bandTitle as bandTitle, " +
                "b.bandIntroduction as bandIntroduction, b.bandRegion as bandRegion, b.bandContent as bandContent, " +
                "IF(vocal is null, 0, vocal) as vocal, IF(guitar is null, 0, guitar) as guitar, " +
                "IF(base is null, 0, base) as base, IF(keyboard is null, 0, keyboard) as keyboard, IF(drum is null, 0, drum) as drum, " +
                "b.chatRoomLink as chatRoomLink, b.performDate as performDate, b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b\n"+
                "WHERE b.bandIdx=? and b.status='ACTIVE'";
        int getBandByIdxParams = bandIdx;
        return this.jdbcTemplate.queryForObject(getBandByIdxQuery,
                                                (rs, rowNum) -> new GetBandRes(
                                                        rs.getInt("bandIdx"),
                                                        rs.getInt("userIdx"),
                                                        rs.getString("bandTitle"),
                                                        rs.getString("bandIntroduction"),
                                                        rs.getString("bandRegion"),
                                                        rs.getString("bandContent"),
                                                        rs.getInt("vocal"),
                                                        rs.getInt("guitar"),
                                                        rs.getInt("base"),
                                                        rs.getInt("keyboard"),
                                                        rs.getInt("drum"),
                                                        rs.getString("chatRoomLink"),
                                                        rs.getString("performDate"),
                                                        rs.getString("bandImgUrl")),
                                                getBandByIdxParams);
    }

    public GetBandRes getBandByIdx(int bandIdx){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, b.bandTitle as bandTitle, " +
                "b.bandIntroduction as bandIntroduction, b.bandRegion as bandRegion, b.bandContent as bandContent, " +
                "IF(vocal is null, 0, vocal) as vocal, IF(guitar is null, 0, guitar) as guitar, " +
                "IF(base is null, 0, base) as base, IF(keyboard is null, 0, keyboard) as keyboard, IF(drum is null, 0, drum) as drum, " +
                "b.performDate as performDate, b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b\n"+
                "WHERE b.bandIdx=? and b.status='ACTIVE'";
        int getBandByIdxParams = bandIdx;
        return this.jdbcTemplate.queryForObject(getBandByIdxQuery,
                                                (rs, rowNum) -> new GetBandRes(
                                                        rs.getInt("bandIdx"),
                                                        rs.getInt("userIdx"),
                                                        rs.getString("bandTitle"),
                                                        rs.getString("bandIntroduction"),
                                                        rs.getString("bandRegion"),
                                                        rs.getString("bandContent"),
                                                        rs.getInt("vocal"),
                                                        rs.getInt("guitar"),
                                                        rs.getInt("base"),
                                                        rs.getInt("keyboard"),
                                                        rs.getInt("drum"),
                                                        null,
                                                        rs.getString("performDate"),
                                                        rs.getString("bandImgUrl")),
                                                getBandByIdxParams);
    }

    // 밴드 생성
    public int insertBand(int userIdx, PostBandReq postBandReq){
        String insertBandQuery = "INSERT INTO Band(userIdx, bandTitle, bandIntroduction, bandRegion, bandContent, vocal, guitar, base, keyboard, drum, chatRoomLink, performDate, bandImgUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertBandParams = new Object[]{ userIdx, postBandReq.getBandTitle(), postBandReq.getBandIntroduction(),
                postBandReq.getBandRegion(), postBandReq.getBandContent(),
                postBandReq.getVocal(), postBandReq.getGuitar(), postBandReq.getBase(), postBandReq.getKeyboard(), postBandReq.getDrum(),
                postBandReq.getChatRoomLink(), postBandReq.getPerformDate(), postBandReq.getBandImgUrl()
        };
        this.jdbcTemplate.update(insertBandQuery, insertBandParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }

    // 밴드 수정
    public int updateBand(int bandIdx, PatchBandReq patchBandReq){
        String updateBandQuery = "UPDATE Band SET bandTitle=?, bandIntroduction=?, bandRegion=?, bandContent=?," +
                "vocal=?, guitar=?, base=?, keyboard=?, drum=?, chatRoomLink=?, performDate=?, bandImgUrl=? WHERE bandIdx = ?" ;
        Object[] updateBandParams = new Object[]{patchBandReq.getBandTitle(), patchBandReq.getBandIntroduction(),
                patchBandReq.getBandRegion(), patchBandReq.getBandContent(),
                patchBandReq.getVocal(), patchBandReq.getGuitar(), patchBandReq.getBase(), patchBandReq.getKeyboard(), patchBandReq.getDrum(),
                patchBandReq.getChatRoomLink(), patchBandReq.getPerformDate(), patchBandReq.getBandImgUrl(), bandIdx};

        return this.jdbcTemplate.update(updateBandQuery,updateBandParams);
    }

    // 밴드 삭제
    public int updateBandStatus(int bandIdx){
        String deleteBandQuery = "UPDATE Band SET status = 'INACTIVE' WHERE bandIdx = ? ";
        Object[] deleteBandParams = new Object[]{bandIdx};

        return this.jdbcTemplate.update(deleteBandQuery,deleteBandParams);
    }
}
