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
        String getSessionMemberQuery = "SELECT BU.session as buSession, BU.userIdx as userIdx, u.nickName as nickName, bandIdx\n" +
                "FROM BandUser as BU JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = BU.userIdx\n" +
                "WHERE bandIdx = ? and status = 'ACTIVE'";
        int getSessionMemberParams = bandIdx;
        return this.jdbcTemplate.query(getSessionMemberQuery,
                                       (rs, rowNum) -> new GetSessionRes(
                                               rs.getInt("buSession"),
                                               rs.getInt("userIdx"),
                                               rs.getString("nickName")),
                                       getSessionMemberParams);
    }

    public List<GetSessionRes> getApplicants(int bandIdx){
        String getApplicantsQuery = "SELECT BU.session as buSession, BU.userIdx as userIdx, u.nickName as nickName, bandIdx\n" +
                "FROM BandUser as BU JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = BU.userIdx\n" +
                "WHERE bandIdx = ? and status = 'INACTIVE'";
        int getBandByIdxParams = bandIdx;
        return this.jdbcTemplate.query(getApplicantsQuery,
                                       (rs, rowNum) -> new GetSessionRes(
                                               rs.getInt("buSession"),
                                               rs.getInt("userIdx"),
                                               rs.getString("nickName")),
                                       getBandByIdxParams);
    }

    // 밴드 조회
    public GetBandRes getMyBandByIdx(int bandIdx,  List<GetSessionRes> sessionMembers, List<GetSessionRes> applicants){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, u.nickName as nickName,\n" +
                "       b.bandTitle as bandTitle, b.bandIntroduction as bandIntroduction,\n" +
                "       b.bandRegion as bandRegion, b.bandContent as bandContent, b.mySession as mySession,\n" +
                "       vocal, vocalComment, guitar, guitarComment, base, baseComment, keyboard, keyboardComment, drum, drumComment,\n" +
                "       b.chatRoomLink as chatRoomLink, b.performDate as performDate, b.performTime as performTime," +
                "       b.performLocation as performLocation, b.performFee as performFee, b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = b.userIdx\n" +
                "WHERE b.bandIdx=? and b.status='ACTIVE'";
        int getBandByIdxParams = bandIdx;
        return this.jdbcTemplate.queryForObject(getBandByIdxQuery,
                                                (rs, rowNum) -> new GetBandRes(
                                                        rs.getInt("bandIdx"),
                                                        rs.getInt("userIdx"),
                                                        rs.getString("nickName"),
                                                        rs.getString("bandTitle"),
                                                        rs.getString("bandIntroduction"),
                                                        rs.getString("bandRegion"),
                                                        rs.getString("bandContent"),
                                                        rs.getInt("mySession"),
                                                        rs.getInt("vocal"),
                                                        rs.getString("vocalComment"),
                                                        rs.getInt("guitar"),
                                                        rs.getString("guitarComment"),
                                                        rs.getInt("base"),
                                                        rs.getString("baseComment"),
                                                        rs.getInt("keyboard"),
                                                        rs.getString("keyboardComment"),
                                                        rs.getInt("drum"),
                                                        rs.getString("drumComment"),
                                                        sessionMembers,
                                                        rs.getString("chatRoomLink"),
                                                        rs.getString("performDate"),
                                                        rs.getString("performTime"),
                                                        rs.getString("performLocation"),
                                                        rs.getInt("performFee"),
                                                        rs.getString("bandImgUrl"),
                                                        applicants),
                                                getBandByIdxParams);
    }

    public GetBandRes getSessionBandByIdx(int bandIdx,  List<GetSessionRes> sessionMembers){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, u.nickName as nickName,\n" +
                "       b.bandTitle as bandTitle, b.bandIntroduction as bandIntroduction,\n" +
                "       b.bandRegion as bandRegion, b.bandContent as bandContent,b.mySession as mySession,\n" +
                "       vocal, vocalComment, guitar, guitarComment, base, baseComment, keyboard, keyboardComment, drum, drumComment,\n" +
                "       b.chatRoomLink as chatRoomLink, b.performDate as performDate, b.performTime as performTime," +
                "       b.performLocation as performLocation, b.performFee as performFee,b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = b.userIdx\n" +
                "WHERE b.bandIdx=? and b.status='ACTIVE'";
        int getBandByIdxParams = bandIdx;
        return this.jdbcTemplate.queryForObject(getBandByIdxQuery,
                                                (rs, rowNum) -> new GetBandRes(
                                                        rs.getInt("bandIdx"),
                                                        rs.getInt("userIdx"),
                                                        rs.getString("nickName"),
                                                        rs.getString("bandTitle"),
                                                        rs.getString("bandIntroduction"),
                                                        rs.getString("bandRegion"),
                                                        rs.getString("bandContent"),
                                                        rs.getInt("mySession"),
                                                        rs.getInt("vocal"),
                                                        rs.getString("vocalComment"),
                                                        rs.getInt("guitar"),
                                                        rs.getString("guitarComment"),
                                                        rs.getInt("base"),
                                                        rs.getString("baseComment"),
                                                        rs.getInt("keyboard"),
                                                        rs.getString("keyboardComment"),
                                                        rs.getInt("drum"),
                                                        rs.getString("drumComment"),
                                                        sessionMembers,
                                                        rs.getString("chatRoomLink"),
                                                        rs.getString("performDate"),
                                                        rs.getString("performTime"),
                                                        rs.getString("performLocation"),
                                                        rs.getInt("performFee"),
                                                        rs.getString("bandImgUrl"),
                                                        null),
                                                getBandByIdxParams);
    }

    public GetBandRes getBandByIdx(int bandIdx,  List<GetSessionRes> sessionMembers){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, u.nickName as nickName,\n" +
                "       b.bandTitle as bandTitle, b.bandIntroduction as bandIntroduction,\n" +
                "       b.bandRegion as bandRegion, b.bandContent as bandContent, b.mySession as mySession,\n" +
                "       vocal, vocalComment, guitar, guitarComment, base, baseComment, keyboard, keyboardComment, drum, drumComment,\n" +
                "       b.performDate as performDate,  b.performTime as performTime," +
                "       b.performLocation as performLocation, b.performFee as performFee, b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = b.userIdx\n" +
                "WHERE b.bandIdx=? and b.status='ACTIVE'";
        int getBandByIdxParams = bandIdx;
        return this.jdbcTemplate.queryForObject(getBandByIdxQuery,
                                                (rs, rowNum) -> new GetBandRes(
                                                        rs.getInt("bandIdx"),
                                                        rs.getInt("userIdx"),
                                                        rs.getString("nickName"),
                                                        rs.getString("bandTitle"),
                                                        rs.getString("bandIntroduction"),
                                                        rs.getString("bandRegion"),
                                                        rs.getString("bandContent"),
                                                        rs.getInt("mySession"),
                                                        rs.getInt("vocal"),
                                                        rs.getString("vocalComment"),
                                                        rs.getInt("guitar"),
                                                        rs.getString("guitarComment"),
                                                        rs.getInt("base"),
                                                        rs.getString("baseComment"),
                                                        rs.getInt("keyboard"),
                                                        rs.getString("keyboardComment"),
                                                        rs.getInt("drum"),
                                                        rs.getString("drumComment"),
                                                        sessionMembers,
                                                        null,
                                                        rs.getString("performDate"),
                                                        rs.getString("performTime"),
                                                        rs.getString("performLocation"),
                                                        rs.getInt("performFee"),
                                                        rs.getString("bandImgUrl"),
                                                        null),
                                                getBandByIdxParams);
    }

    // 밴드 생성
    public int insertBand(int userIdx, PostBandReq postBandReq){
        String insertBandQuery = "INSERT INTO Band(userIdx, bandTitle, bandIntroduction, bandRegion, bandContent, mySession, vocal, vocalComment, " +
                "guitar, guitarComment, base, baseComment, keyboard, keyboardComment, drum, drumComment, chatRoomLink, bandImgUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertBandParams = new Object[]{ userIdx, postBandReq.getBandTitle(), postBandReq.getBandIntroduction(),
                postBandReq.getBandRegion(), postBandReq.getBandContent(), postBandReq.getMySession(),
                postBandReq.getVocal(), postBandReq.getVocalComment(), postBandReq.getGuitar(), postBandReq.getGuitarComment(),
                postBandReq.getBase(), postBandReq.getBaseComment(),postBandReq.getKeyboard(), postBandReq.getKeyboardComment(),postBandReq.getDrum(),postBandReq.getDrumComment(),
                postBandReq.getChatRoomLink(), postBandReq.getBandImgUrl()
        };
        this.jdbcTemplate.update(insertBandQuery, insertBandParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int insertMy(int userIdx, int bandIdx, int buSession){
        String insertApplyQuery = "INSERT INTO BandUser(userIdx, bandIdx, buSession) VALUES (?, ?, ?)";
        Object[] insertApplyParams = new Object[]{ userIdx, bandIdx, buSession };
        this.jdbcTemplate.update(insertApplyQuery, insertApplyParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 밴드 수정
    public int updateBand(int bandIdx, PatchBandReq patchBandReq){
        String updateBandQuery = "UPDATE Band SET bandTitle=?, bandIntroduction=?, bandRegion=?, bandContent=?, mySession=?," +
                "vocal=?, vocalComment=?, guitar=?, guitarComment=?, base=?, baseComment=?, keyboard=?, keyboardComment=?, drum=?, drumComment=?, chatRoomLink=?, performDate=?, performTime=?, performLocation=?, performFee=?, bandImgUrl=? WHERE bandIdx = ?" ;
        Object[] updateBandParams = new Object[]{ patchBandReq.getBandTitle(), patchBandReq.getBandIntroduction(),
                patchBandReq.getBandRegion(), patchBandReq.getBandContent(), patchBandReq.getMySession(),
                patchBandReq.getVocal(), patchBandReq.getVocalComment(), patchBandReq.getGuitar(), patchBandReq.getGuitarComment(),
                patchBandReq.getBase(), patchBandReq.getBaseComment(),patchBandReq.getKeyboard(), patchBandReq.getKeyboardComment(),patchBandReq.getDrum(),patchBandReq.getDrumComment(),
                patchBandReq.getChatRoomLink(), patchBandReq.getPerformDate(), patchBandReq.getPerformTime(), patchBandReq.getPerformLocation(), patchBandReq.getPerformFee(),patchBandReq.getBandImgUrl(), bandIdx };

        return this.jdbcTemplate.update(updateBandQuery,updateBandParams);
    }

    // 밴드 삭제
    public int updateBandStatus(int bandIdx){
        String deleteBandQuery = "UPDATE Band SET status = 'INACTIVE' WHERE bandIdx = ? ";
        Object[] deleteBandParams = new Object[]{ bandIdx };

        return this.jdbcTemplate.update(deleteBandQuery,deleteBandParams);
    }


    // 밴드 세션 지원
    public int insertApply(int userIdx, int bandIdx, PostApplyReq postApplyReq){
        String insertApplyQuery = "INSERT INTO BandUser(userIdx, bandIdx, buSession) VALUES (?, ?, ?)";
        Object[] insertApplyParams = new Object[]{ userIdx, bandIdx, postApplyReq.getBuSession() };
        this.jdbcTemplate.update(insertApplyQuery, insertApplyParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 세션 지원 수락
    public int acceptSession(int bandIdx, int userIdx){
        String updateStatusQuery = "UPDATE BandUser SET status = 'ACTIVE' WHERE bandIdx = ? and userIdx = ?";
        Object[] updateStatusParams = new Object[]{ bandIdx, userIdx };

        return this.jdbcTemplate.update(updateStatusQuery, updateStatusParams);
    }

    // 세션 지원 거절
    public int rejectSession(int bandIdx, int userIdx){
        String updateStatusQuery = "UPDATE BandUser SET status = 'REJECT' WHERE bandIdx = ? and userIdx = ?";
        Object[] updateStatusParams = new Object[]{ bandIdx, userIdx };

        return this.jdbcTemplate.update(updateStatusQuery, updateStatusParams);
    }
}
