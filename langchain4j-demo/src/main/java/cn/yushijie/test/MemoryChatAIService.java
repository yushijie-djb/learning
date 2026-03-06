package cn.yushijie.test;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * @ClassName MemoryChatAIService
 * @Description 对话记忆AIService
 * @Author yu155
 * @Date 2026/3/4 14:47
 * @Version 1.0.0
 */
public class MemoryChatAIService {
    public static void main(String[] args) {
        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("gpt-4")
                .build();

        Assistant assistant = AiServices.builder(Assistant.class).chatMemory(MessageWindowChatMemory.withMaxMessages(3)).chatModel(model).build();
        String result = assistant.chat("我的名字是什么");
        System.out.println(result);
        String result2 = assistant.chat("我的名字是余世杰");
        System.out.println(result2);
        String result3 = assistant.chat("现在告诉我我的名字");
        System.out.println(result3);
    }
}
