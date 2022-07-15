package com.example.demo.src.S33;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @ResponseBody
    @PostMapping("/api/v1/upload")
    //public String uploadImage(@RequestPart MultipartFile file) {
    public BaseResponse<String> uploadImage(@RequestPart MultipartFile file) {

        String result = fileUploadService.uploadImage(file);
        return new BaseResponse<>(result);

    }

}