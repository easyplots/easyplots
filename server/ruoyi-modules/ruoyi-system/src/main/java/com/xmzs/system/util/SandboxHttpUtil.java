package com.xmzs.system.util;

import cn.hutool.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @Description
 * @Author lkp
 * @Date 2024/7/11 17:58
 */
public class SandboxHttpUtil {

    public static Request createRequest(String code) {
        String url = "http://localhost:8060/eval";

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("input", code);

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonObject.toString(), JSON);
        return  new Request.Builder()
            .url(url)
            .post(body)
//            .header("Authorization","Bearer " +apiKey.get(0))
            .build();
    }



}
