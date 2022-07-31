package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.src.search.model.GetSearchBandRes;
import com.example.demo.src.search.model.GetSearchLesRes;
import com.example.demo.src.search.model.GetSearchUserRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;


@Service
public class SearchProvider {

    private final SearchDao searchDao;
    private final JwtService jwtService;

    @Autowired
    public SearchProvider(SearchDao searchDao, JwtService jwtService) {
        this.searchDao = searchDao;
        this.jwtService = jwtService;

    }

    /**
     * 상단바 유저 검색
     */
    public List<GetSearchUserRes> getSearchUser(String keyword) throws BaseException {

        try {
            List<GetSearchUserRes> getSearchUser = searchDao.getSearchUser(keyword);
            return getSearchUser;

        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 상단바 밴드 검색
     */
    public List<GetSearchBandRes> getSearchBand(String keyword) throws BaseException {

        try {
            List<GetSearchBandRes> getSearchBand = searchDao.getSearchBand(keyword);
            return getSearchBand;

        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 상단바 레슨 검색
     */
    public List<GetSearchLesRes> getSearchLes(String keyword) throws BaseException {

        try {
            List<GetSearchLesRes> getSearchLes = searchDao.getSearchLes(keyword);
            return getSearchLes;

        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
