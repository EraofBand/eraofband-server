package com.example.demo.src.home;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.home.model.GetFameLessonRes;
import com.example.demo.src.lesson.LessonProvider;
import com.example.demo.src.lesson.LessonService;
import com.example.demo.src.lesson.model.*;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private final HomeProvider homeProvider;
    @Autowired
    private final HomeService homeService;
    @Autowired
    private final JwtService jwtService;

    public HomeController(HomeProvider homeProvider, HomeService homeService, JwtService jwtService){
        this.homeProvider = homeProvider;
        this.homeService = homeService;
        this.jwtService = jwtService;
    }

    // 인기 TOP3 레슨
    @ResponseBody
    @GetMapping("/lesson/fame") // (get) https://eraofband.shop/home/lesson/fame
    @ApiOperation(value = "인기 TOP3 레슨 정보 반환")
    public BaseResponse<List<GetFameLessonRes>> getFameLesson(){
        try{

            List<GetFameLessonRes> getFameLessonRes = homeProvider.getFameLesson();
            return new BaseResponse<>(getFameLessonRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
