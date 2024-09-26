package com.xmzs.controller;

import com.xmzs.system.domain.vo.SysOssVo;
import com.xmzs.system.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @Description
 * @Author lkp
 * @Date 2024/7/11 17:21
 */
@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final ISysOssService sysOssService;

    public static MultipartFile convert(String base64String, String fileName) {
        // 解码Base64字符串为字节数组
        byte[] bytes = Base64.getDecoder().decode(base64String);

        // 创建MockMultipartFile对象并设置文件内容和文件名
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName, "application/octet-stream", bytes);

        return multipartFile;
    }


//    @GetMapping("/imgTest")
//    public SysOssVo imgTest() {
//        String base64String = "";
//        String filePath = "output.png"; // 输出文件路径
//
//        MultipartFile file = TestController.convert(base64String, filePath);
//
//        SysOssVo ossVo = sysOssService.upload(file);
//
//        return ossVo;
//    }
}
