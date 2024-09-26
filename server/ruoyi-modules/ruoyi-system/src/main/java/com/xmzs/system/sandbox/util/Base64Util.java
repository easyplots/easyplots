package com.xmzs.system.sandbox.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * @Description
 * @Author lkp
 * @Date 2024/7/11 18:23
 */
public class Base64Util {

    public static MultipartFile convert(String base64String, String fileName) {
        // 解码Base64字符串为字节数组
        byte[] bytes = Base64.getDecoder().decode(base64String);

        // 创建MockMultipartFile对象并设置文件内容和文件名
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName, "application/octet-stream", bytes);
        return multipartFile;
    }




}
