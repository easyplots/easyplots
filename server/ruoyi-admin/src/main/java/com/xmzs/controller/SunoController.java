package com.xmzs.controller;

import cn.hutool.json.JSONUtil;

import com.xmzs.system.domain.GenerateLyric;
import com.xmzs.system.domain.GenerateSuno;
import com.xmzs.system.service.IChatCostService;
import com.xmzs.system.util.OkHttpUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sunoapi")
@RequiredArgsConstructor
@Slf4j
public class SunoController {

    private final IChatCostService chatCostService;

    private final OkHttpUtil okHttpUtil;

    @Value("${suno.generate}")
    private String generate;

    @ApiOperation(value = "文生歌曲")
    @PostMapping("/generate")
    public String generate(@RequestBody GenerateSuno generateSuno) {
        // 扣除接口费用并且保存消息记录
        chatCostService.taskDeduct("suno","文生歌曲", NumberUtils.toDouble(generate, 0.3));
        // 创建请求体（这里使用JSON作为媒体类型）
        String generateJson = JSONUtil.toJsonStr(generateSuno);
        String url = "suno/generate";
        Request request = okHttpUtil.createPostRequest(url, generateJson);
        return okHttpUtil.executeRequest(request);
    }

    @ApiOperation(value = "生成歌词")
    @PostMapping("/generate/lyrics/")
    public String generate(@RequestBody GenerateLyric generateLyric) {
        String generateJson = JSONUtil.toJsonStr(generateLyric);
        String url = "task/suno/v1/submit/lyrics";
        Request request = okHttpUtil.createPostRequest(url, generateJson);
        return okHttpUtil.executeRequest(request);
    }


    @ApiOperation(value = "查询歌词任务")
    @GetMapping("/lyrics/{taskId}")
    public String lyrics(@PathVariable String taskId) {
        String url = "task/suno/v1/fetch/"+taskId;
        Request request = okHttpUtil.createGetRequest(url);
        return okHttpUtil.executeRequest(request);
    }


    @ApiOperation(value = "查询歌曲任务")
    @GetMapping("/feed/{taskId}")
    public String feed(@PathVariable String taskId) {
        String url = "suno/feed/"+taskId;
        Request request = okHttpUtil.createGetRequest(url);
        return okHttpUtil.executeRequest(request);
    }

}
