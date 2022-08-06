package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.SendPushMessage;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    private int result;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService, ObjectMapper objectMapper) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

        this.objectMapper = objectMapper;
    }

    /**
     * 카카오 서버에서 이메일 가져오기
     */
    public String getKakaoInfo(String token) throws BaseException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";
        String email = "";
        //access_token을 이용하여 사용자 정보 조회
        try {

            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            //JsonElement element =  Jsonparser.parseString(result);

            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();

            if (hasEmail) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            System.out.println("email : " + email);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return email;
    }

    /**
     * 회원가입 및 jwt 생성
     */
    public PostUserRes createUser(PostUserReq postUserReq, String email) throws BaseException {
        try {
            int userIdx = userDao.createUser(postUserReq, email);
            //jwt 발급
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 로그인 및 jwt 생성
     */
    public PostLoginRes logIn(String email) throws BaseException {
        try {
            //이미 회원가입이 되어 있을 경우
            if (userProvider.checkEmail(email) == 1) {
                User user = userDao.getUserIdx(email);
                int userIdx = user.getUserIdx();

                //새 jwt 발급
                String jwt = jwtService.createJwt(userIdx);
                return new PostLoginRes(userIdx, jwt);
            }

            //회원가입이 되어 있지 않은 경우
            return new PostLoginRes(0, "NULL");

        }catch(Exception exception){
                System.out.println(exception);
                throw new BaseException(DATABASE_ERROR);
            }
    }

    /**
     * 회원 정보 변경
     */
    public void modifyUserInfo(PatchUserReq patchUserReq) throws BaseException {
        try {
            result = userDao.modifyUserInfo(patchUserReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

        if (result == 0) {
            throw new BaseException(MODIFY_FAIL_USER);
        }
    }

    /**
     * 회원 세션 변경
     */
    public void modifyUserSession(PatchSessionReq patchSessionReq) throws BaseException {
        try {
            result = userDao.modifyUserSession(patchSessionReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (result == 0) {
            throw new BaseException(MODIFY_FAIL_SESSION);
        }
    }

    /**
     * 회원 삭제
     */
    public void deleteUser ( int userIdx) throws BaseException {
        try {
            result = userDao.deleteUser(userIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(DELETE_FAIL_USER);
        }
    }

    /**
     * 팔로우 하기
     */
    public PostFollowRes followUser(int myIdx, int userIdx) throws BaseException {

        try{
            if(userProvider.checkUserExist(userIdx) == 0){
                throw new BaseException(USERS_EMPTY_USER_ID);
            }
            result = userDao.updateFollow(myIdx, userIdx);
            if(result == 0){
                throw new BaseException(FOLLOW_FAIL_USER);
            }
            //팔로우 요청자의 정보 얻기
            GetUserNotiInfoRes getUserNotiInfoRes=userDao.Noti(myIdx);
            //알림 테이블에 추가
            userDao.followNoti(getUserNotiInfoRes, userIdx);
            return new PostFollowRes(result);
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }



    private final ObjectMapper objectMapper;
    public void sendMessageTo(int myIdx, int userIdx, String title, String body) throws IOException {
        String API_URL = "https://fcm.googleapis.com/v1/projects/eraofband-5bbf4/messages:send";
        //팔로우 요청자의 정보 얻기
        GetUserNotiInfoRes getUserNotiInfoRes=userDao.Noti(myIdx);

        GetUserTokenRes getUserTokenRes= userDao.getFCMToken(userIdx);
        SendPushMessage sendPushMessage=new SendPushMessage(objectMapper);
        String message = sendPushMessage.makeMessage(getUserTokenRes.getToken(), title, getUserNotiInfoRes.getNickName()+body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + sendPushMessage.getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        //System.out.println(response.body().string());
    }


    /**
     * 팔로우 취소 하기
     */
    public void unfollowUser(int myIdx, int userIdx) throws BaseException {
        try{
            result = userDao.updateUnFollow(myIdx, userIdx);

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

        if(result == 0){
            throw new BaseException(UNFOLLOW_FAIL_USER);
        }

    }
}