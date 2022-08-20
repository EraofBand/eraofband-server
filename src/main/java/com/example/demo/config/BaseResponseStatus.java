package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    CHAT_SEND_FAIL_BLOCKED(false,2004,"채팅을 보낼 수 없는 유저입니다."),
    COMMENT_FAIL_BLOCKED(false,2006,"상대방이 회원님을 차단하여 댓글을 등록할 수 없습니다."),
    UNBLOCK_FAIL_USER(false,2077,"유저 차단 해제에 실패했습니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),



    // band
    POSTS_EMPTY_BAND_ID(false,2020,"밴드 아이디 값을 확인해주세요."),
    POST_BANDS_EMPTY_CONTENTS(false,2021,"내용 입력값을 확인해주세요."),
    POST_BANDS_INVALID_CONTENTS(false,2022,"내용의 글자수를 확인해주세요."),
    POST_BANDS_EMPTY_IMG(false,2023,"이미지를 입력해주세요."),
    MODIFY_FAIL_BAND(false,2024,"밴드 변경에 실패했습니다."),
    DELETE_FAIL_BAND(false,2025,"밴드 삭제에 실패했습니다."),
    LIKES_FAIL_BAND(false,2026,"밴드 좋아요에 실패했습니다."),
    UNLIKES_FAIL_BAND(false,2027,"밴드 좋아요 취소에 실패했습니다."),

    ALREADY_BAND(false, 2030, "이미 지원한 밴드입니다."),
    NOT_BAND_MEMBER(false, 2031, "등록하지 않은 밴드입니다."),
    WITHDRAW_FAIL_BAND(false,2032,"밴드 탈퇴에 실패했습니다."),

    POSTS_EMPTY_ALBUM_ID(false,2033,"밴드 앨범 아이디 값을 확인해주세요."),

    DELETE_FAIL_ALBUM(false,2034,"밴드 앨범 삭제에 실패했습니다."),

    // lesson
    DELETE_FAIL_LESSON(false,2040,"레슨 삭제에 실패했습니다."),


    POSTS_EMPTY_LESSON_ID(false,2050,"레슨 아이디 값을 확인해주세요."),

    MODIFY_FAIL_LESSON(false,2051,"레슨 수정에 실패했습니다."),

    LIKES_FAIL_LESSON(false,2052,"레슨 좋아요에 실패했습니다."),

    ALREADY_LESSON(false, 2053, "이미 지원한 레슨입니다."),

    NOT_LESSON_MEMBER(false, 2054, "등록하지 않은 레슨입니다."),

    WITHDRAW_FAIL_LESSON(false,2055,"레슨 탈퇴에 실패했습니다."),

    UNLIKES_FAIL_LESSON(false,2056,"레슨 좋아요 취소에 실패했습니다."),


    // pofol
    POSTS_EMPTY_POFOL_ID(false,2060,"포트폴리오 아이디 값을 확인해주세요."),
    POSTS_EMPTY_POFOL_COMMENT_ID(false,2061,"포트폴리오 댓글 아이디 값을 확인해주세요."),
    POST_POSTS_INVALID_CONTENTS(false,2062,"내용의 글자수를 확인해주세요."),

    POST_POSTS_EMPTY_VIDEOURL(false,2063,"포트폴리오 동영상을 입력해주세요."),
    MODIFY_FAIL_POFOL(false,2064,"포트폴리오 수정에 실패했습니다."),
    DELETE_FAIL_POFOL(false,2065,"포트폴리오 삭제에 실패했습니다."),

    DELETE_FAIL_POFOL_COMMENT(false,2066,"포트폴리오 댓글 삭제에 실패했습니다."),

    LIKES_FAIL_POFOL(false,2067,"포트폴리오 좋아요에 실패했습니다."),

    UNLIKES_FAIL_POFOL(false,2068,"포트폴리오 좋아요 취소에 실패했습니다."),


    // follow
    FOLLOW_FAIL_USER(false,2070,"유저 팔로우에 실패했습니다."),
    UNFOLLOW_FAIL_USER(false,2071,"유저 팔로우 취소에 실패했습니다."),


    // notice
    DELETE_FAIL_NOTICE(false,2080,"알림 삭제에 실패했습니다."),


    //chat
    POSTS_EMPTY_CHAT_ID(false,2090,"채팅방 아이디 값을 확인해주세요."),
    DELETE_FAIL_CHAT(false,2091,"채팅방 삭제에 실패했습니다."),

    POST_FAIL_CHAT(false,2092,"채팅방 생성에 실패했습니다."),

    // board
    POSTS_EMPTY_BOARD_ID(false,2100,"게시글 아이디 값을 확인해주세요."),
    POST_BOARD_EMPTY_TITLE(false,2101,"게시글 제목을 입력해주세요."),
    MODIFY_FAIL_BOARD(false,2102,"게시글 수정에 실패했습니다."),
    DELETE_FAIL_BOARD(false,2103,"게시글 삭제에 실패했습니다."),
    ADD_FAIL_BOARD(false,2104,"게시글 조회 수 증가에 실패했습니다."),
    POSTS_EMPTY_BOARD_COMMENT_ID(false,2105,"게시글 댓글 아이디 값을 확인해주세요."),
    DELETE_FAIL_BOARD_COMMENT(false,2106,"게시글 댓글 삭제에 실패했습니다."),
    DUPLICATED_BOARD_LIKE(false,2108,"이미 추천한 게시글입니다."),
    MODIFY_FAIL_BOARD_IMG(false,2109,"게시글 사진 수정에 실패했습니다."),
    DELETE_FAIL_BOARD_IMG(false,2110,"게시글 사진 삭제에 실패했습니다."),
    UNLIKES_FAIL_BOARD(false,2111,"게시글 좋아요 취소에 실패했습니다."),




    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USER(false,4014,"회원 정보 수정에 실패하였습니다."),
    MODIFY_FAIL_SESSION(false,4015,"회원 정보 수정에 실패하였습니다."),
    DELETE_FAIL_USER(false,4016,"회원 삭제에 실패하였습니다."),
    LOGOUT_FAIL_USER(false,4017,"회원 로그아웃에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    //MODIFY_FAIL_POFOL(false,4020,"포트폴리오 수정 실패"),
    //DELETE_FAIL_POFOL(false,4021,"포트폴리오 삭제 실패");

    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}