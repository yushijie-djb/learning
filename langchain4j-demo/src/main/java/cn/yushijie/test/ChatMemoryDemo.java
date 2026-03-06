package cn.yushijie.test;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * @ClassName ChatMemory
 * @Description // TODO:
 * @Author yu155
 * @Date 2026/3/2 18:17
 * @Version 1.0.0
 */
public class ChatMemoryDemo {
    public static void main(String[] args) {

        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

        ChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("gpt-4")
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(3);

        chatMemory.add(UserMessage.from("Hello, my name is YuShiJie"));
        AiMessage answer = model.chat(chatMemory.messages()).aiMessage();
        System.out.println(answer.text()); //
        chatMemory.add(answer);

        chatMemory.add(UserMessage.from("What is my name?"));
        AiMessage answerWithName = model.chat(chatMemory.messages()).aiMessage();
        System.out.println(answerWithName.text()); //
        chatMemory.add(answerWithName);

    }
}
