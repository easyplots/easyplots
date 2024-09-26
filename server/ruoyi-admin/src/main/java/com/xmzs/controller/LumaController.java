package com.xmzs.controller;

import cn.hutool.json.JSONUtil;
import com.xmzs.system.domain.GenerateLuma;
import com.xmzs.system.service.IChatCostService;
import com.xmzs.system.util.OkHttpUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 描述：文生视频
 *
 * @author ageerle@163.com
 * date 2024/6/27
 */
@RestController
@RequestMapping("/luma")
@RequiredArgsConstructor
@Slf4j
public class LumaController {

    private final OkHttpUtil okHttpUtil;

    private final IChatCostService chatCostService;

    @Value("${luma.generate}")
    private String generate;

    @ApiOperation(value = "文生视频")
    @PostMapping("/generations/")
    public String generate(@RequestBody GenerateLuma generateLuma) {
        chatCostService.taskDeduct("luma","文生视频", NumberUtils.toDouble(generate, 0.3));
        String generateJson = JSONUtil.toJsonStr(generateLuma);
        String url = "luma/generations/";
        Request request = okHttpUtil.createPostRequest(url, generateJson);
        return okHttpUtil.executeRequest(request);
    }

    @ApiOperation(value = "文生视频任务查询")
    @GetMapping("/generations/{taskId}")
    public String generate(@PathVariable String taskId) {
        String url = "luma/generations/"+taskId;
        Request request = okHttpUtil.createGetRequest(url);
        return okHttpUtil.executeRequest(request);
    }

}
