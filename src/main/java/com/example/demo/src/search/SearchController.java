package com.example.demo.src.search;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.search.model.GetSearchBandRes;
import com.example.demo.src.search.model.GetSearchLesRes;
import com.example.demo.src.search.model.GetSearchUserRes;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private final SearchProvider searchProvider;
    @Autowired
    private final SearchService searchService;
    @Autowired
    private final JwtService jwtService;

    public SearchController(SearchProvider searchProvider, SearchService searchService, JwtService jwtService){
        this.searchProvider = searchProvider;
        this.searchService = searchService;
        this.jwtService = jwtService;
    }

    /**
     * 상단바 유저 검색 API
     * [GET] /search/users/{keyword}
     * @return BaseResponse<List<GetSearchUserRes>>
     */
    @ResponseBody
    @GetMapping("/users/{keyword}") // (get) https://eraofband.shop/search/users/해리
    @ApiOperation(value = "상단바 유저 검색")
    @ApiImplicitParam(name="keyword", value="검색할 유저", required = true)
    public BaseResponse<List<GetSearchUserRes>> getSearchUser(@PathVariable("keyword") String keyword){
        try{
            List<GetSearchUserRes> getSearchUserRes = searchProvider.getSearchUser(keyword);
            return new BaseResponse<>(getSearchUserRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상단바 밴드 검색 API
     * [GET] /search/bands/{keyword}
     * @return BaseResponse<List<GetSearchBandRes>>
     */
    @ResponseBody
    @GetMapping("/bands/{keyword}") // (get) https://eraofband.shop/search/bands/락밴드
    @ApiOperation(value = "상단바 밴드 검색")
    @ApiImplicitParam(name="keyword", value="검색할 밴드", required = true)
    public BaseResponse<List<GetSearchBandRes>> getSearchBand(@PathVariable("keyword") String keyword){
        try{

            List<GetSearchBandRes> getSearchBandRes = searchProvider.getSearchBand(keyword);
            return new BaseResponse<>(getSearchBandRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상단바 레슨 검색 API
     * [GET] /search/lessons/{keyword}
     * @return BaseResponse<List<GetSearchLesRes>>
     */
    @ResponseBody
    @GetMapping("/lessons/{keyword}") // (get) https://eraofband.shop/search/lessons/기타레슨
    @ApiOperation(value = "상단바 레슨 검색")
    @ApiImplicitParam(name="keyword", value="검색할 레슨", required = true)
    public BaseResponse<List<GetSearchLesRes>> getSearchLes(@PathVariable("keyword") String keyword){
        try{

            List<GetSearchLesRes> getSearchLesRes = searchProvider.getSearchLes(keyword);
            return new BaseResponse<>(getSearchLesRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
