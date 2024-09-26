package com.xmzs.system.listener;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.common.chat.config.LocalCache;
import com.xmzs.common.chat.entity.chat.ChatChoice;
import com.xmzs.common.chat.entity.chat.ChatCompletionResponse;
import com.xmzs.common.chat.entity.chat.Message;
import com.xmzs.common.chat.utils.TikTokensUtil;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.utils.SpringUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.system.domain.bo.ChatMessageBo;
import com.xmzs.system.domain.bo.SysModelBo;
import com.xmzs.system.domain.vo.SysModelVo;
import com.xmzs.system.domain.vo.SysOssVo;
import com.xmzs.system.sandbox.bo.SnekboxResult;
import com.xmzs.system.sandbox.util.Base64Util;
import com.xmzs.system.service.IChatMessageService;
import com.xmzs.system.service.IChatCostService;
import com.xmzs.system.service.ISysModelService;
import com.xmzs.system.service.ISysOssService;
import com.xmzs.system.util.OkHttpUtil;
import com.xmzs.system.util.SandboxHttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 描述：OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SSEEventSourceListener extends EventSourceListener {

    private ResponseBodyEmitter emitter;

    private StringBuilder stringBuffer = new StringBuilder();

    private ISysOssService sysOssService;

    @Autowired(required = false)
    public SSEEventSourceListener(ResponseBodyEmitter emitter,ISysOssService sysOssService) {
        this.emitter = emitter;
        this.sysOssService = sysOssService;
    }
    private static final ISysModelService sysModelService = SpringUtils.getBean(ISysModelService.class);
    private String modelName;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立sse连接...");
    }


    public static String exactCode(String data) {
        // todo 目前只解析第一段代码，以后支持如果多段，自动合并。
        int start = data.indexOf("```python\n");
        if (start < 0) {
            // 没有代码
            return null;
        }
        String t = data.substring(start + 10);
        t = t.substring(0, t.indexOf("```"));
        return t;
    }



    private R<String> runPythonCode(String responseMessage) {
        // 提取代码
        String code = exactCode(stringBuffer.toString());
        if (StringUtils.isEmpty(code)) {
            return R.ok();
        }

        // todo 完善逻辑：不同的包，不同的变量名，保存的图片的代码不一样。
        // 增加一行代码：plt.savefig('chart.png')
        code = code + "\nplt.savefig('chart.png')";

        // 执行代码
        OkHttpUtil okHttpUtil = new OkHttpUtil();
        Request request = SandboxHttpUtil.createRequest(code);
        String result = okHttpUtil.executeRequest(request);
        SnekboxResult snekboxResult = JSONUtil.toBean(result, SnekboxResult.class);

        if (snekboxResult.getReturncode() != 0) {
            return R.fail(snekboxResult.getStdout());
        }
        if (snekboxResult.getFiles().isEmpty()) {
            return R.ok();
        }
        String base64String = snekboxResult.getFiles().get(0).getContent();
        // 图片上传到阿里云
        String filePath = "output.png"; // 输出文件路径

        MultipartFile file = Base64Util.convert(base64String, filePath);

        SysOssVo ossVo = sysOssService.upload(file);

        return R.ok("",ossVo.getUrl());
    }

    private String buildMarkdownImgLink(String url) {
        return String.format("![](%s)", url);
    }



    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(@NotNull EventSource eventSource, String id, String type, String data) {
        try {
//            System.out.println("data: "+data);
            if (data.equals("[DONE]")) {
                R<String> result = runPythonCode(stringBuffer.toString());

                String content = "\n";
                if (R.isSuccess(result)) {
                    // 绘图结果返回给前端
                    content += buildMarkdownImgLink(result.getData());
                } else {
                    content += result.getMsg();
                    System.out.println(result.getMsg());
                }
                Message message = new Message();
                message.setContent(content);

                ChatChoice choice = new ChatChoice();
                choice.setDelta(message);

                ChatCompletionResponse response = new ChatCompletionResponse();
                response.setChoices(List.of(choice));
                emitter.send(JSON.toJSONString(response));

//                String t1 = "{\"id\":\"chatcmpl-9jeqKXHRjVYbXi5DAx1TixnYuaybb\",\"object\":\"chat.completion.chunk\",\"created\":1720668788,\"model\":\"gpt-3.5-turbo-0125\",\"system_fingerprint\":null,\"choices\":[{\"index\":0,\"delta\":{\"content\":\"hello\"},\"logprobs\":null,\"finish_reason\":null}]}";
//
//                emitter.send(t1);
//                TimeUnit.SECONDS.sleep(5);
//                String t = "{\"id\":\"chatcmpl-9jeqKXHRjVYbXi5DAx1TixnYuaybb\",\"object\":\"chat.completion.chunk\",\"created\":1720668788,\"model\":\"gpt-3.5-turbo-0125\",\"system_fingerprint\":null,\"choices\":[{\"index\":0,\"delta\":{\"content\":\"\\n![](https://aoss.cn-sh-01.sensecoreapi-oss.cn/nova-clotho-code-interpreter/b9595268-096f-40b2-977d-76cb193af666.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=F55CF26308EB4EC1860E56C9884519FD%2F20240711%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240711T043453Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=00018de6b4163d38905193c59d8e7adf7ac3ef848e2199f83d17d0dc4577cafe)\\n\"},\"logprobs\":null,\"finish_reason\":null}]}";
//
//                emitter.send(t);

                emitter.complete();
                if(StringUtils.isNotEmpty(modelName)){
                    IChatCostService IChatCostService = SpringUtils.context().getBean(IChatCostService.class);
                    IChatMessageService chatMessageService = SpringUtils.context().getBean(IChatMessageService.class);
                    ChatMessageBo chatMessageBo = new ChatMessageBo();
                    chatMessageBo.setModelName(modelName);
                    chatMessageBo.setContent(stringBuffer.toString());
                    Long userId = (Long)LocalCache.CACHE.get("userId");
                    chatMessageBo.setUserId(userId);

                    //查询按次数扣费的模型
                    SysModelBo sysModelBo = new SysModelBo();
                    sysModelBo.setModelType("2");
                    sysModelBo.setModelName(modelName);
                    List<SysModelVo> sysModelList = sysModelService.queryList(sysModelBo);
                    if (CollectionUtil.isNotEmpty(sysModelList)){
                        chatMessageBo.setDeductCost(0d);
                        chatMessageBo.setRemark("提问时扣费");
                        // 保存消息记录
                        chatMessageService.insertByBo(chatMessageBo);
                    }else{
                        int tokens = TikTokensUtil.tokens(modelName,stringBuffer.toString());
                        chatMessageBo.setTotalTokens(tokens);
                        // 按token扣费并且保存消息记录
                        IChatCostService.deductToken(chatMessageBo);
                    }
                }
                return;
            }
            // 解析返回内容
            ObjectMapper mapper = new ObjectMapper();
            ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
            if(completionResponse == null || CollectionUtil.isEmpty(completionResponse.getChoices())){
                return;
            }
            String content = completionResponse.getChoices().get(0).getDelta().getContent();
            if(StringUtils.isEmpty(content)){
                return;
            }
            if(StringUtils.isEmpty(modelName)){
                modelName = completionResponse.getModel();
            }
            stringBuffer.append(content);
            emitter.send(data);
        } catch (Exception e) {
            log.error("sse信息推送失败{}内容：{}",e.getMessage(),data);
            eventSource.cancel();
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("OpenAI关闭sse连接...");
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
    }

}
