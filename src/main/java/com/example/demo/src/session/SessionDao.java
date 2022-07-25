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
        String selectUserIdxQuery = "SELECT userIdx FROM Band WHERE bandIdx = ? and status='ACTIVE'";
        int selectUserIdxParams = bandIdx;
        return this.jdbcTemplate.queryForObject(selectUserIdxQuery,
                                                int.class,
                                                selectUserIdxParams);
    }

    public int checkBandSession(int userIdx, int bandIdx){
        String checkUserExistQuery = "SELECT exists(SELECT bandUserIdx FROM BandUser WHERE userIdx=? and bandIdx=? and status='ACTIVE')";
        Object[] checkUserExistParams = new Object[]{ userIdx, bandIdx };
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                                                       int.class,
                                                       checkUserExistParams);
    }

    // 밴드 확인
    public int checkBandExist(int bandIdx){
        String checkBandExistQuery = "SELECT exists(SELECT bandIdx FROM Band WHERE bandIdx = ? and status='ACTIVE')";
        int checkBandExistParams = bandIdx;
        return this.jdbcTemplate.queryForObject(checkBandExistQuery,
                                                int.class,
                                                checkBandExistParams);
    }

    // 최신 밴드
    public List<GetNewBandRes> getNewBand(){
        String getNewBandQuery = "SELECT b.bandIdx, bandRegion, bandTitle, bandImgUrl, sessionNum, vocal+guitar+base+keyboard+drum as totalNum\n" +
                "FROM Band as b\n" +
                "    left join (SELECT bandIdx, count(bandIdx) as sessionNum \n" +
                "    from BandUser WHERE status = 'ACTIVE') BU on BU.bandIdx = b.bandIdx\n" +
                "WHERE status = 'ACTIVE' order by createdAt DESC\n" +
                "LIMIT 6;";
        Object[] getNewBandParams = new Object[]{};
        return this.jdbcTemplate.query(getNewBandQuery,
                                       (rs, rowNum) -> new GetNewBandRes(
                                               rs.getInt("bandIdx"),
                                               rs.getString("bandRegion"),
                                               rs.getString("bandTitle"),
                                               rs.getInt("sessionNum"),
                                               rs.getInt("totalNum"),
                                               rs.getString("bandImgUrl")
                                       ),
                                       getNewBandParams);
    }

    // 인기 TOP3 밴드
    public List<GetFameBandRes> getFameBand(){
        String getFameBandQuery = "SELECT b.bandIdx as bandIdx, b.bandTitle as bandTitle, b.bandIntroduction as bandIntroduction, b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b\n" +
                "    left join (select bandIdx, userIdx, count(bandLikeIdx) as bandLikeCount\n" +
                "    from BandLike WHERE status = 'ACTIVE' group by bandIdx) plc on plc.bandIdx = b.bandIdx\n" +
                "WHERE b.status='ACTIVE' order by bandLikeCount DESC\n" +
                "LIMIT 3;";
        Object[] getFameBandParams = new Object[]{};
        return this.jdbcTemplate.query(getFameBandQuery,
                                       (rs, rowNum) -> new GetFameBandRes(
                                               rs.getInt("bandIdx"),
                                               rs.getString("bandTitle"),
                                               rs.getString("bandIntroduction"),
                                               rs.getString("bandImgUrl")
                                       ),
                                       getFameBandParams);
    }

    public List<GetSessionMemRes> getSessionMembers(int bandIdx){
        String getSessionMemberQuery = "SELECT BU.buSession as buSession, BU.userIdx as userIdx, u.nickName as nickName, u.profileImgUrl as profileImgUrl, u.introduction as introduction\n" +
                "FROM BandUser as BU\n" +
                "JOIN (SELECT userIdx, nickName, profileImgUrl, introduction FROM User) u on u.userIdx = BU.userIdx\n" +
                "WHERE bandIdx = ? and status = 'ACTIVE'";
        int getSessionMemberParams = bandIdx;
        return this.jdbcTemplate.query(getSessionMemberQuery,
                                       (rs, rowNum) -> new GetSessionMemRes(
                                               rs.getInt("buSession"),
                                               rs.getInt("userIdx"),
                                               rs.getString("profileImgUrl"),
                                               rs.getString("nickName"),
                                               rs.getString("introduction")),
                                       getSessionMemberParams);
    }

    public List<GetSessionAppRes> getApplicants(int bandIdx){
        String getApplicantsQuery = "SELECT BU.buSession as buSession, BU.userIdx as userIdx, u.nickName as nickName, u.profileImgUrl as profileImgUrl, u.introduction as introduction,\n" +
                "case\n" +
                "when timestampdiff(second, BU.updatedAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(second, BU.updatedAt, current_timestamp), '초 전')\n" +
                "when timestampdiff(minute , BU.updatedAt, current_timestamp) < 60\n" +
                "then concat(timestampdiff(minute, BU.updatedAt, current_timestamp), '분 전')\n" +
                "when timestampdiff(hour , BU.updatedAt, current_timestamp) < 24\n" +
                "then concat(timestampdiff(hour, BU.updatedAt, current_timestamp), '시간 전')\n" +
                "when timestampdiff(day , BU.updatedAt, current_timestamp) < 365\n" +
                "then concat(timestampdiff(day, BU.updatedAt, current_timestamp), '일 전')\n" +
                "else timestampdiff(year , BU.updatedAt, current_timestamp)\n" +
                "end as updatedAt\n" +
                "FROM BandUser as BU\n" +
                "JOIN(SELECT userIdx, nickName, profileImgUrl, introduction FROM User) u on u.userIdx = BU.userIdx\n" +
                "WHERE bandIdx = ? and status = 'WAIT'";
        int getBandByIdxParams = bandIdx;
        return this.jdbcTemplate.query(getApplicantsQuery,
                                       (rs, rowNum) -> new GetSessionAppRes(
                                               rs.getInt("buSession"),
                                               rs.getInt("userIdx"),
                                               rs.getString("profileImgUrl"),
                                               rs.getString("nickName"),
                                               rs.getString("introduction"),
                                               rs.getString("updatedAt")),
                                       getBandByIdxParams);
    }

    // 밴드 조회
    public GetBandRes getMyBandByIdx(int bandIdx, List<GetSessionMemRes> sessionMembers, List<GetSessionAppRes> applicants){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, u.nickName as nickName,\n" +
                "       b.bandTitle as bandTitle, b.bandIntroduction as bandIntroduction,\n" +
                "       b.bandRegion as bandRegion, b.bandContent as bandContent, b.mySession as mySession,\n" +
                "       b.vocal-IF(b0.vocalCount is null, 0, b0.vocalCount) as vocal, vocalComment, b.guitar-IF(b1.guitarCount is null, 0, b1.guitarCount) as guitar, guitarComment, b.base-IF(b2.baseCount is null, 0, b2.baseCount) as base,\n" +
                "       baseComment, b.keyboard-IF(b3.keyboardCount is null, 0, b3.keyboardCount) as keyboard, keyboardComment, b.drum-IF(b4.drumCount is null, 0, b4.drumCount) as drum, drumComment,\n" +
                "       b.chatRoomLink as chatRoomLink, b.performDate as performDate, b.performTime as performTime," +
                "       b.performLocation as performLocation, b.performFee as performFee, b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = b.userIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as vocalCount from BandUser where status='ACTIVE' and buSession=0 group by bandIdx) b0 on b0.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as guitarCount from BandUser where status='ACTIVE' and buSession=1 group by bandIdx) b1 on b1.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as baseCount from BandUser where status='ACTIVE' and buSession=2 group by bandIdx) b2 on b2.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as keyboardCount from BandUser where status='ACTIVE' and buSession=3 group by bandIdx) b3 on b3.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as drumCount from BandUser where status='ACTIVE' and buSession=4 group by bandIdx) b4 on b4.bandIdx=b.bandIdx\n" +
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

    public GetBandRes getSessionBandByIdx(int bandIdx,  List<GetSessionMemRes> sessionMembers){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, u.nickName as nickName,\n" +
                "       b.bandTitle as bandTitle, b.bandIntroduction as bandIntroduction,\n" +
                "       b.bandRegion as bandRegion, b.bandContent as bandContent,b.mySession as mySession,\n" +
                "       b.vocal-IF(b0.vocalCount is null, 0, b0.vocalCount) as vocal, vocalComment, b.guitar-IF(b1.guitarCount is null, 0, b1.guitarCount) as guitar, guitarComment, b.base-IF(b2.baseCount is null, 0, b2.baseCount) as base,\n" +
                "       baseComment, b.keyboard-IF(b3.keyboardCount is null, 0, b3.keyboardCount) as keyboard, keyboardComment, b.drum-IF(b4.drumCount is null, 0, b4.drumCount) as drum, drumComment,\n" +
                "       b.chatRoomLink as chatRoomLink, b.performDate as performDate, b.performTime as performTime," +
                "       b.performLocation as performLocation, b.performFee as performFee,b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = b.userIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as vocalCount from BandUser where status='ACTIVE' and buSession=0 group by bandIdx) b0 on b0.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as guitarCount from BandUser where status='ACTIVE' and buSession=1 group by bandIdx) b1 on b1.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as baseCount from BandUser where status='ACTIVE' and buSession=2 group by bandIdx) b2 on b2.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as keyboardCount from BandUser where status='ACTIVE' and buSession=3 group by bandIdx) b3 on b3.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as drumCount from BandUser where status='ACTIVE' and buSession=4 group by bandIdx) b4 on b4.bandIdx=b.bandIdx\n" +
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

    public GetBandRes getBandByIdx(int bandIdx,  List<GetSessionMemRes> sessionMembers){
        String getBandByIdxQuery = "SELECT b.bandIdx as bandIdx, b.userIdx as userIdx, u.nickName as nickName,\n" +
                "       b.bandTitle as bandTitle, b.bandIntroduction as bandIntroduction,\n" +
                "       b.bandRegion as bandRegion, b.bandContent as bandContent, b.mySession as mySession,\n" +
                "       b.vocal-IF(b0.vocalCount is null, 0, b0.vocalCount) as vocal, vocalComment, b.guitar-IF(b1.guitarCount is null, 0, b1.guitarCount) as guitar, guitarComment, b.base-IF(b2.baseCount is null, 0, b2.baseCount) as base,\n" +
                "       baseComment, b.keyboard-IF(b3.keyboardCount is null, 0, b3.keyboardCount) as keyboard, keyboardComment, b.drum-IF(b4.drumCount is null, 0, b4.drumCount) as drum, drumComment,\n" +
                "       b.performDate as performDate,  b.performTime as performTime," +
                "       b.performLocation as performLocation, b.performFee as performFee, b.bandImgUrl as bandImgUrl\n" +
                "FROM Band as b JOIN (SELECT userIdx, nickName FROM User) u on u.userIdx = b.userIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as vocalCount from BandUser where status='ACTIVE' and buSession=0 group by bandIdx) b0 on b0.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as guitarCount from BandUser where status='ACTIVE' and buSession=1 group by bandIdx) b1 on b1.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as baseCount from BandUser where status='ACTIVE' and buSession=2 group by bandIdx) b2 on b2.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as keyboardCount from BandUser where status='ACTIVE' and buSession=3 group by bandIdx) b3 on b3.bandIdx=b.bandIdx\n" +
                "   left join (select bandIdx, count(bandUserIdx) as drumCount from BandUser where status='ACTIVE' and buSession=4 group by bandIdx) b4 on b4.bandIdx=b.bandIdx\n" +
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


    // 밴드 수정
    public int updateBand(int bandIdx, PatchBandReq patchBandReq){
        String updateBandQuery = "UPDATE Band SET bandTitle=?, bandIntroduction=?, bandRegion=?, bandContent=?, mySession=?," +
                "vocal=?, vocalComment=?, guitar=?, guitarComment=?, base=?, baseComment=?, keyboard=?, keyboardComment=?, drum=?, drumComment=?, chatRoomLink=?, performDate=?, performTime=?, performLocation=?, performFee=?, bandImgUrl=? WHERE bandIdx = ? and status='ACTIVE'" ;
        Object[] updateBandParams = new Object[]{ patchBandReq.getBandTitle(), patchBandReq.getBandIntroduction(),
                patchBandReq.getBandRegion(), patchBandReq.getBandContent(), patchBandReq.getMySession(),
                patchBandReq.getVocal(), patchBandReq.getVocalComment(), patchBandReq.getGuitar(), patchBandReq.getGuitarComment(),
                patchBandReq.getBase(), patchBandReq.getBaseComment(),patchBandReq.getKeyboard(), patchBandReq.getKeyboardComment(),patchBandReq.getDrum(),patchBandReq.getDrumComment(),
                patchBandReq.getChatRoomLink(), patchBandReq.getPerformDate(), patchBandReq.getPerformTime(), patchBandReq.getPerformLocation(), patchBandReq.getPerformFee(),patchBandReq.getBandImgUrl(), bandIdx };

        return this.jdbcTemplate.update(updateBandQuery,updateBandParams);
    }

    // 밴드 삭제
    public int updateBandStatus(int bandIdx){
        String deleteBandQuery = "update Band b" +
                "    left join BandUser as bu on (bu.bandIdx=b.bandIdx)\n" +
                "    left join BandLike as bl on (bl.bandIdx=b.bandIdx)\n" +
                "        set b.status='INACTIVE',\n" +
                "            bu.status='INACTIVE',\n" +
                "            bl.status='INACTIVE'\n" +
                "   where b.bandIdx = ? ";
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

    // 밴드 좋아요
    public int updateLikes(int userIdx, int bandIdx) {
        String updateLikesQuery = "INSERT INTO BandLike(userIdx, bandIdx) VALUES (?,?)";
        Object[] updateLikesParams = new Object[]{userIdx, bandIdx};

        this.jdbcTemplate.update(updateLikesQuery, updateLikesParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 밴드 좋아요 취소
    public int updateUnlikes(int userIdx, int bandIdx) {
        String updateUnlikesQuery = "DELETE FROM BandLike WHERE userIdx = ? and bandIdx = ?";
        Object[] updateUnlikesParams = new Object[]{userIdx, bandIdx};

        return this.jdbcTemplate.update(updateUnlikesQuery, updateUnlikesParams);
    }

    // 찜한 밴드 조회
    public List<GetLikesBandRes> getLikesBand(int userIdx){
        String getLikesBandQuery = "\n"+
                "SELECT b.bandIdx as bandIdx, b.bandImgUrl as bandImgUrl, b.bandTitle as bandTitle,"+
                "        b.bandIntroduction as bandIntroduction, b.bandRegion as bandRegion,"+
                "        IF(memberCount is null, 0, memberCount) as memberCount, b.vocal+b.guitar+b.base+b.keyboard+b.drum+1 as capacity\n"+
                "        FROM BandUser as bu\n"+
                "        left join Band as b on b.bandIdx=bu.bandIdx\n"+
                "        left join (select bandIdx, count(bandUserIdx) as memberCount from BandUser where status='ACTIVE' group by bandIdx) bm on bm.bandIdx=b.bandIdx\n"+
                "        left join BandLike as bl on b.bandIdx = bl.bandIdx\n"+
                "        WHERE b.status='ACTIVE' and bu.status='ACTIVE' and bl.userIdx=?\n"+
                "        group by b.bandIdx\n"+
                "        order by b.bandIdx";
        Object[] getLikesBandParams = new Object[]{userIdx};
        return this.jdbcTemplate.query(getLikesBandQuery,
                (rs, rowNum) -> new GetLikesBandRes(
                        rs.getInt("bandIdx"),
                        rs.getString("bandImgUrl"),
                        rs.getString("bandTitle"),
                        rs.getString("bandIntroduction"),
                        rs.getString("bandRegion"),
                        rs.getInt("capacity"),
                        rs.getInt("memberCount")),
                getLikesBandParams);
    }

    public List<GetInfoBandRes> getInfoBandRes(String region, String session){

        String getInfoBandQuery="";
        Object[] getInfoBandParams=new Object[]{};
        if (region.compareTo("전체") == 0 && session.compareTo("전체") == 0) {
            getInfoBandParams = new Object[]{};

        } else if (region.compareTo("전체") == 0 && session.compareTo("전체") != 0) {
            getInfoBandParams = new Object[]{session};

        } else if (region.compareTo("전체") != 0 && session.compareTo("전체") == 0) {
            getInfoBandParams = new Object[]{region};

        } else if (region.compareTo("전체") != 0 && session.compareTo("전체") != 0) {
            getInfoBandQuery = "\n"+
                    "SELECT b.bandIdx as bandIdx, b.bandImgUrl as bandImgUrl, b.bandTitle as bandTitle,"+
                    "        b.bandIntroduction as bandIntroduction, b.bandRegion as bandRegion,"+
                    "        IF(memberCount is null, 0, memberCount) as memberCount, b.vocal+b.guitar+b.base+b.keyboard+b.drum+1 as capacity," +
                    "        b.vocal-IF(b0.vocalCount is null, 0, b0.vocalCount) as vocal"+
                    "        FROM BandUser as bu\n"+
                    "        JOIN Band as b on b.bandIdx=bu.bandIdx\n"+
                    "        left join (select bandIdx, count(bandUserIdx) as memberCount from BandUser where status='ACTIVE' group by bandIdx) bm on bm.bandIdx=b.bandIdx\n"+
                    "        left join (select bandIdx, count(bandUserIdx) as vocalCount from BandUser where status='ACTIVE' and buSession=0 group by bandIdx) b0 on b0.bandIdx=b.bandIdx\n" +
                    "        WHERE b.status='ACTIVE' and bu.status='ACTIVE' and (SUBSTRING(b.bandRegion, 1, 2)) = ? and COLUMN_NAME =?\n" +
                    "        group by b.bandIdx\n"+
                    "        order by b.bandIdx";
            getInfoBandParams = new Object[]{region,session};
        }
        return this.jdbcTemplate.query(getInfoBandQuery,
                (rs, rowNum) -> new GetInfoBandRes(
                        rs.getInt("bandIdx"),
                        rs.getString("bandImgUrl"),
                        rs.getString("bandTitle"),
                        rs.getString("bandIntroduction"),
                        rs.getString("bandRegion"),
                        rs.getInt("capacity"),
                        rs.getInt("memberCount")),
                getInfoBandParams);
    }
}
