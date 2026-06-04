package cn.yushijie.test;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;

/**
 * @ClassName Test1
 * @Description // TODO:
 * @Author yu155
 * @Date 2026/3/2 17:22
 * @Version 1.0.0
 */
public class HelloChat {
    public static void main(String[] args) {
        String baseUrl = "http://10.0.1.253:11434";
        String apiKey = "ollama";

        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName("qwen3.5:9b")
                .logRequests(true)
                .timeout(Duration.ofMinutes(10L))
                .build();

//        QwenChatModel qwenChatModel = QwenChatModel.builder().baseUrl(baseUrl).apiKey(apiKey).modelName("qwen3.5")
//                .build();

        ChatRequest request = ChatRequest.builder()
                .messages(UserMessage.from("介绍下你自己"))
                .parameters(ChatRequestParameters.builder()
                        .temperature(0.5)
                        .build())
                .build();

        ChatResponse chatResponse = chatModel.chat(request);
        // 简化调用
//        String answer = model.chat("介绍一下你自己吧");
        System.out.println(chatResponse); // Hello World

    }
}
