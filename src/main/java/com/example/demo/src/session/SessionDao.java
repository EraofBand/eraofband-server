package com.example.demo.src.session;

import com.example.demo.src.session.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SessionDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 유저 확인
    public int checkBandMaker(int bandIdx){
        String selectUserIdxQuery = "SELECT userIdx FROM Band WHERE bandIdx = ?";
        int selectUserIdxParams = bandIdx;
        return this.jdbcTemplate.queryForObject(selectUserIdxQuery,
                                                int.class,
                                                selectUserIdxParams);
    }

    public int checkBandSession(int userIdx, int bandIdx){
        String checkUserExistQuery = "SELECT exists(SELECT bandUserIdx FROM BandUser WHERE userIdx=? and bandIdx=?)";
        Object[] checkUserExistParams = new Object[]{ userIdx, bandIdx };
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                                                       int.class,
                                                       checkUserExistParams);
    }

    // 밴드 확인
    public int checkBandExist(int bandIdx){
        String checkBandExistQuery = "SELECT exists(SELECT bandIdx FROM Band WHERE bandIdx = ?)";
        int checkBandExistParams = bandIdx;
        return this.jdbcTemplate.queryForObject(checkBandExistQuery,
                                                int.class,
                                                checkBandExistParams);
    }

    public List<GetSessionRes> getSessionMembers(int bandIdx){
        String getSessionMemberQuery = "SELECT session, userIdx, bandIdx\n" +
                "        FROM BandUser\n" +
                "        WHERE bandIdx=? and status = 'ACTIVE'";
        int getSessionMemberParams = bandIdx;
        return this.jdbcTemplate.query(getSessionMemberQuery,
                                       (rs, rowNum) -> new GetSessionRes(
                                               rs.getInt("session"),
                                               rs.getInt("userIdx")),
                                       getSessionMemberParams);
    }

    public List<GetSessionRes> getApplicants(int bandIdx){
        String getApplicantsQuery = "SELECT session, userIdx, bandIdx\n" +
                "        FROM BandUser\n" +
                "        WHERE bandIdx=? and status = 'INACTIVE'";
        int getBandByIdxParams = bandIdx;
        return this.jdbcTemplate.query(getApplicantsQuery,
                                       (rs, rowNum) -> new GetSessionRes(
                                               rs.getInt("session"),
                                               rs.getInt("userIdx")),
                                       getBandByIdxParams);
    }

    // 밴드 조회
    public GetBandRes getMyBandByIdx(int bandIdx,  List<GetSessionRes> sessionMembers, List<GetSessionRes> applicants){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, b.bandTitle as bandTitle,\n" +
                "    b.bandIntroduction as bandIntroduction, b.bandRegion as bandRegion,\n" +
                "    b.bandContent as bandContent,\n" +
                "    IF(vocal is null, 0, vocal) as vocal,\n" +
                "    IF(guitar is null, 0, guitar) as guitar,\n" +
                "    IF(base is null, 0, base) as base,\n" +
                "    IF(keyboard is null, 0, keyboard) as keyboard,\n" +
                "    IF(drum is null, 0, drum) as drum,\n" +
                "    b.chatRoomLink as chatRoomLink, b.performDate as performDate,\n" +
                "    b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b\n" +
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
                                                        sessionMembers,
                                                        rs.getString("chatRoomLink"),
                                                        rs.getString("performDate"),
                                                        rs.getString("bandImgUrl"),
                                                        applicants),
                                                getBandByIdxParams);
    }

    public GetBandRes getSessionBandByIdx(int bandIdx,  List<GetSessionRes> sessionMembers){
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
                                                        sessionMembers,
                                                        rs.getString("chatRoomLink"),
                                                        rs.getString("performDate"),
                                                        rs.getString("bandImgUrl"),
                                                        null),
                                                getBandByIdxParams);
    }

    public GetBandRes getBandByIdx(int bandIdx,  List<GetSessionRes> sessionMembers){
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
                                                        sessionMembers,
                                                        null,
                                                        rs.getString("performDate"),
                                                        rs.getString("bandImgUrl"),
                                                        null),
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

    // 밴드 세션 지원
    public int insertApply(int userIdx, int bandIdx, PostApplyReq postApplyReq){
        String insertApplyQuery = "INSERT INTO BandUser(userIdx, bandIdx, session) VALUES (?, ?, ?)";
        Object[] insertApplyParams = new Object[]{ userIdx, bandIdx, postApplyReq.getSession() };
        this.jdbcTemplate.update(insertApplyQuery, insertApplyParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
}
