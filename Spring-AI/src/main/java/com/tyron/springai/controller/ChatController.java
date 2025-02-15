package com.tyron.springai.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * 聊天控制类
 */

@RestController()
@RequestMapping("/ai")
public class ChatController {

    private OpenAiChatModel openAiChatModel;

    @Autowired
    public ChatController(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    /**
     * 生成
     *
     * @param message 输入信息
     * @return 输出信息
     */
    @GetMapping("/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        return Map.of("generation", this.openAiChatModel.call(message));
    }

    /**
     * 流式调用
     */
    @GetMapping("/generateStream")
    public SseEmitter streamChat(@RequestParam String message) {
        // 创建 SSE 发射器，设置超时时间（例如 1 分钟）
        SseEmitter emitter = new SseEmitter(60_000L);
        // 创建 Prompt 对象
        Prompt prompt = new Prompt(new UserMessage(message));
        // 订阅流式响应
        // 完成处理
        // 异常处理
        openAiChatModel.stream(prompt).subscribe(response -> {
                    try {
                        String content = response.getResult().getOutput().getContent();
                        System.out.print(content);
                        // 发送 SSE 事件
                        emitter.send(SseEmitter.event()
                                .data(content)
                                .id(String.valueOf(System.currentTimeMillis()))
                                .build());
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError,
                emitter::complete
        );
        // 处理客户端断开连接
        emitter.onCompletion(() -> {
            // 可在此处释放资源
            System.out.println("SSE connection completed");
        });
        emitter.onTimeout(() -> {
            emitter.complete();
            System.out.println("SSE connection timed out");
        });
        return emitter;
    }
}
