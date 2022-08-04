package com.example.demo.src.search;
import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.model.*;



import com.example.demo.src.search.model.GetSearchBandRes;
import com.example.demo.src.search.model.GetSearchLesRes;
import com.example.demo.src.search.model.GetSearchUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;




@Repository
public class SearchDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 상단바 유저 검색
     */
    public List<GetSearchUserRes> getSearchUser(String search){
        String getSearchUserQuery = "\n"+
                "select u.userIdx as userIdx, u.nickName as nickName,u.profileImgUrl as profileImgUrl,u.userSession as userSession" +
                "        from User as u\n" +
                "        where u.status='ACTIVE' and u.nickName LIKE CONCAT('%', ?, '%')\n" +
                "        group by u.userIdx\n" +
                "        order by u.userIdx";

        Object[] getSearchUserParams = new Object[]{search};

        return this.jdbcTemplate.query(getSearchUserQuery,
                (rs, rowNum) -> new GetSearchUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickName"),
                        rs.getInt("userSession")),
                getSearchUserParams);

    }

    /**
     * 상단바 밴드 검색
     */
    public List<GetSearchBandRes> getSearchBand(String search){

        String getSearchBandQuery = "\n"+
                "SELECT b.bandIdx as bandIdx, b.bandImgUrl as bandImgUrl, b.bandTitle as bandTitle,\n" +
                    "                        b.bandIntroduction as bandIntroduction, b.bandRegion as bandRegion,\n" +
                    "                        IF(memberCount is null, 0, memberCount) as memberCount, b.vocal+b.guitar+b.base+b.keyboard+b.drum+1 as capacity\n" +
                    "                        FROM BandUser as bu\n" +
                    "                        join Band as b\n" +
                    "                        left join (select bandIdx, count(bandUserIdx) as memberCount from BandUser where status='ACTIVE' group by bandIdx) bm on bm.bandIdx=b.bandIdx\n" +
                    "                        WHERE b.status='ACTIVE' and bu.status='ACTIVE' and b.bandTitle LIKE CONCAT('%', ?, '%')\n" +
                    "                        group by b.bandIdx\n" +
                    "                        order by b.bandIdx DESC";
        Object[] getSearchBandParams = new Object[]{search};

        return this.jdbcTemplate.query(getSearchBandQuery,
                (rs, rowNum) -> new GetSearchBandRes(
                        rs.getInt("bandIdx"),
                        rs.getString("bandImgUrl"),
                        rs.getString("bandTitle"),
                        rs.getString("bandIntroduction"),
                        rs.getString("bandRegion"),
                        rs.getInt("capacity"),
                        rs.getInt("memberCount")),
                getSearchBandParams);

    }

    /**
     * 상단바 레슨 검색
     */
    public List<GetSearchLesRes> getSearchLes(String search){

        String getSearchLessonQuery = "\n"+
                "SELECT l.lessonIdx as lessonIdx, l.lessonImgUrl as lessonImgUrl, l.lessonTitle as lessonTitle,\n" +
                "                l.lessonIntroduction as lessonIntroduction, l.lessonRegion as lessonRegion,\n" +
                "                IF(memberCount is null, 0, memberCount) as memberCount, l.capacity as capacity\n" +
                "                FROM LessonUser as lu\n" +
                "                JOIN Lesson as l\n" +
                "                left join (select lessonIdx, count(lessonUserIdx) as memberCount from LessonUser where status='ACTIVE' group by lessonIdx) lm on lm.lessonIdx=l.lessonIdx\n" +
                "                WHERE l.status='ACTIVE' and lu.status='ACTIVE' and l.lessonTitle LIKE CONCAT('%', ?, '%')\n" +
                "                group by l.lessonIdx\n" +
                "                order by l.lessonIdx DESC";
        Object[] getSearchLessonParams = new Object[]{search};

        return this.jdbcTemplate.query(getSearchLessonQuery,
                (rs, rowNum) -> new GetSearchLesRes(
                        rs.getInt("lessonIdx"),
                        rs.getString("lessonImgUrl"),
                        rs.getString("lessonTitle"),
                        rs.getString("lessonIntroduction"),
                        rs.getString("lessonRegion"),
                        rs.getInt("capacity"),
                        rs.getInt("memberCount")),
                getSearchLessonParams);

    }


}
