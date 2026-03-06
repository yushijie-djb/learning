package cn.yushijie.test;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * @ClassName Test1
 * @Description // TODO:
 * @Author yu155
 * @Date 2026/3/2 17:22
 * @Version 1.0.0
 */
public class HelloChat {
    public static void main(String[] args) {
        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("gpt-4")
                .build();

        ChatRequest request = ChatRequest.builder()
                .messages(UserMessage.from("介绍一下你自己吧"))
                .parameters(ChatRequestParameters.builder()
                        .temperature(0.5)
                        .build())
                .build();

        ChatResponse chatResponse = model.chat(request);
        // 简化调用
//        String answer = model.chat("介绍一下你自己吧");
        System.out.println(chatResponse); // Hello World

    }
}
