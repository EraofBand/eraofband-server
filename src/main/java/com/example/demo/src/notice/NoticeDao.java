package com.example.demo.src.notice;
import com.example.demo.config.BaseException;



import com.example.demo.src.notice.model.GetNoticeRes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class NoticeDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 알람 조회
     * */
    public List<GetNoticeRes> getMyNotice(int userIdx) {

        String getMyNoticeQuery = "\n" +
                "SELECT n.noticeIdx as noticeIdx, n.image as noticeImg, n.head as noticeHead, n.body as noticeBody, case\n" +
                "                                when timestampdiff(second, n.createdAt, current_timestamp) < 60\n" +
                "                                    then concat(timestampdiff(second, n.createdAt, current_timestamp), '초 전')\n" +
                "                                when timestampdiff(minute, n.createdAt, current_timestamp) < 60\n" +
                "                                    then concat(timestampdiff(minute, n.createdAt, current_timestamp), '분 전')\n" +
                "                                when timestampdiff(hour, n.createdAt, current_timestamp) < 24\n" +
                "                                    then concat(timestampdiff(hour, n.createdAt, current_timestamp), '시간 전')\n" +
                "                                when timestampdiff(day, n.createdAt, current_timestamp) < 365\n" +
                "                                    then concat(timestampdiff(day, n.createdAt, current_timestamp), '일 전')\n" +
                "                                else timestampdiff(year, n.createdAt, current_timestamp)\n" +
                "                            end as updatedAt, status\n" +
                "FROM Notice as n WHERE n.receiverIdx=? ORDER BY createdAt DESC\n" +
                "LIMIT 30;\n";
        int getMyNoticeParam = userIdx;
        return this.jdbcTemplate.query(getMyNoticeQuery,
                (rs, rowNum) -> new GetNoticeRes(
                        rs.getInt("noticeIdx"),
                        rs.getString("noticeImg"),
                        rs.getString("noticeHead"),
                        rs.getString("noticeBody"),
                        rs.getString("updatedAt"),
                        rs.getString("status")
                ), getMyNoticeParam);

    }

    /**
     * 알림 INACTIVE
     * */
    public int updateNoticeStatus(int userIdx) {
        String deleteUserQuery = "update Notice n\n" +
                "set n.status='INACTIVE'\n" +
                "where n.receiverIdx = ?";
        Object[] deleteUserParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(deleteUserQuery, deleteUserParams);
    }

    /**
     * 알림 전체 삭제
     */
    public int deleteNotice(int userIdx) {
        String deleteNoticeQuery = "DELETE FROM Notice WHERE receiverIdx = ?";
        Object[] deleteNoticeParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(deleteNoticeQuery, deleteNoticeParams);
    }


}
