package com.example.demo.src.user;


import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

//    public List<GetUserRes> getUsers(){
//        String getUsersQuery = "select userIdx,name,nickName from User";
//        return this.jdbcTemplate.query(getUsersQuery,
//                (rs,rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("name"),
//                        rs.getString("nickName")
//                ));
//    }
//
//    public GetUserRes getUsersByEmail(String email){
//        String getUsersByEmailQuery = "select userIdx,name,nickName from User where email=?";
//        String getUsersByEmailParams = email;
//        return this.jdbcTemplate.queryForObject(getUsersByEmailQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("name"),
//                        rs.getString("nickName")),
//                getUsersByEmailParams);
//    }


    public GetUserRes getUsersByIdx(int userIdx){
        String getUsersByIdxQuery = "select u.userIdx as userIdx, u.nickName as nickName,u.gender as gender,u.birth as birth,u.introduction as introduction,u.profileImgUrl as profileImgUrl,u.session as session,u.region as region," +
                        "IF(pofolCount is null, 0, pofolCount) as pofolCount,IF(followerCount is null, 0, followerCount) as followerCount,IF(followeeCount is null, 0, followeeCount) as followeeCount\n"+
        "from User as u\n"+
            "left join (select userIdx, count(pofolIdx) as pofolCount from Pofol where status='ACTIVE' group by userIdx) p on p.userIdx=u.userIdx\n"+
            "left join (select followerIdx, count(followIdx) as followerCount from Follow where status='ACTIVE' group by followerIdx) fr on fr.followerIdx=u.userIdx\n"+
            "left join (select followeeIdx, count(followIdx) as followeeCount from Follow where status='ACTIVE' group by followeeIdx) fe on fe.followeeIdx=u.userIdx\n"+
        "where u.userIdx=? and u.status='ACTIVE'";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("gender"),
                        rs.getString("birth"),
                        rs.getString("introduction"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("session"),
                        rs.getString("region"),
                        rs.getInt("followerCount"),
                        rs.getInt("followeeCount"),
                        rs.getInt("pofolCount")),
                getUsersByIdxParams);
    }

    /**회원가입*/
    public int createUser(PostUserReq postUserReq, String email){
        String createUserQuery = "insert into User (nickName,email,birth,gender,profileImgUrl,session,region) VALUES (?,?,?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getNickName(),email,postUserReq.getBirth(), postUserReq.getGender(),
                postUserReq.getProfileImgUrl(),postUserReq.getSession(),postUserReq.getRegion()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }




}
