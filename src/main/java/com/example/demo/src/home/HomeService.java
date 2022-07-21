package com.example.demo.src.home;

import com.example.demo.config.BaseException;
import com.example.demo.src.lesson.LessonDao;
import com.example.demo.src.lesson.LessonProvider;
import com.example.demo.src.lesson.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class HomeService {

    private final HomeDao homeDao;
    private final HomeProvider homeProvider;
    private final JwtService jwtService;

    @Autowired
    public HomeService(HomeDao homeDao, HomeProvider homeProvider, JwtService jwtService) {
        this.homeDao = homeDao;
        this.homeProvider = homeProvider;
        this.jwtService = jwtService;
    }



}
