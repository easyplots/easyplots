package com.xmzs.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelUtil;
import com.xmzs.common.chat.domain.request.ChatRequest;
import com.xmzs.common.chat.domain.request.Dall3Request;
import com.xmzs.common.chat.entity.Tts.TextToSpeech;
import com.xmzs.common.chat.entity.files.UploadFileResponse;
import com.xmzs.common.chat.entity.images.Item;
import com.xmzs.common.chat.entity.whisper.WhisperResponse;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.domain.vo.ChatMessageVo;
import com.xmzs.system.service.IChatMessageService;
import com.xmzs.system.service.ISseService;
import com.xmzs.system.util.OkHttpUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.util.List;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @date 2023-03-01
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ISseService ISseService;

    private final IChatMessageService chatMessageService;

    /**
     * 聊天接口
     */
    @PostMapping("/chat")
    @ResponseBody
    public SseEmitter sseChat(@RequestBody @Valid ChatRequest chatRequest, HttpServletResponse response) {
        return ISseService.sseChat(chatRequest);
    }

    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        try (InputStream in = file.getInputStream();
             OutputStream out = new FileOutputStream(convFile)) {
            byte[] bytes = new byte[1024];
            int read;
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            return convFile;
        }
    }
    /**
     * 解析excel文件为文本
     *
     */
    @Log(title = "parseTableData", businessType = BusinessType.INSERT)
    @PostMapping(value = "/parseTableData")
    @ResponseBody
    public R<String> parseTableData(@RequestPart("file") MultipartFile multipartFile) {
        if (ObjectUtil.isNull(multipartFile)) {
            return R.fail("文件不能为空");
        }
        try {
            String sheetData = ExcelUtil.getReader(multipartFile.getInputStream()).readAsText(false);
            return R.ok("", sheetData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args){

        ChatController.test();

    }

    public static void test() {
        OkHttpUtil okHttpUtil = new OkHttpUtil();


        String code = "import matplotlib.pyplot as plt\n\n# 数据\ncategories = ['A', 'B', 'C', 'D']\nvalues = [20, 35, 30, 25]\n\n# 创建柱状图\nplt.bar(categories, values)\n\n# 添加标题和标签\nplt.title('Sample Bar Chart')\nplt.xlabel('Categories')\nplt.ylabel('Values')\n\n# 显示图形\nplt.savefig('my_figure.png')";

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("input", code);

        String url = "http://localhost:8060/eval";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonObject.toString(), JSON);
        Request request =  new Request.Builder()
            .url(url)
            .post(body)
//            .header("Authorization","Bearer " +apiKey.get(0))
            .build();

        String result = okHttpUtil.executeRequest(request);
        System.out.println(result);
    }


    /**
     * 上传文件
     */
    @PostMapping("/v1/upload")
    @ResponseBody
    public UploadFileResponse upload(@RequestPart("file") MultipartFile file) {
        return ISseService.upload(file);
    }


    /**
     * 语音转文本
     *
     * @param file
     */
    @PostMapping("/audio")
    @ResponseBody
    public WhisperResponse audio(@RequestParam("file") MultipartFile file) {
        WhisperResponse whisperResponse = ISseService.speechToTextTranscriptionsV2(file);
        return whisperResponse;
    }

    /**
     * 文本转语音
     *
     * @param textToSpeech
     */
    @PostMapping("/speech")
    @ResponseBody
    public ResponseEntity<Resource> speech(@RequestBody TextToSpeech textToSpeech) {
        return ISseService.textToSpeed(textToSpeech);
    }


    @PostMapping("/dall3")
    @ResponseBody
    public R<List<Item>> dall3(@RequestBody @Valid Dall3Request request) {
        return R.ok(ISseService.dall3(request));
    }

    /**
     * 聊天记录
     */
    @PostMapping("/chatList")
    @ResponseBody
    public R<TableDataInfo<ChatMessageVo>> list(@RequestBody @Valid ChatMessageBo chatRequest, @RequestBody PageQuery pageQuery) {
        // 默认查询当前登录用户消息记录
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw new BaseException("用户未登录！");
        }
        chatRequest.setUserId(loginUser.getUserId());
        TableDataInfo<ChatMessageVo> chatMessageVoTableDataInfo = chatMessageService.queryPageList(chatRequest, pageQuery);
        return R.ok(chatMessageVoTableDataInfo);
    }

}
