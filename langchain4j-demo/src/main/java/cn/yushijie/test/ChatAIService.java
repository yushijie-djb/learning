package cn.yushijie.test;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;

import java.util.List;

/**
 * @ClassName ChatAIService
 * @Description // TODO:
 * @Author yu155
 * @Date 2026/3/3 18:16
 * @Version 1.0.0
 */
public class ChatAIService {
    public static void main(String[] args) throws InterruptedException {
        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

//        OpenAiChatModel model = OpenAiChatModel.builder()
//                .baseUrl(baseUrl)
//                .apiKey(apiKey)
//                .modelName("gpt-4")
//                .build();

//        Assistant assistant = AiServices.create(Assistant.class, model);
//        String res = assistant.chat("你好，我的名字是余世杰");
//        System.out.println(res);

        OpenAiStreamingChatModel streamingChatModel = OpenAiStreamingChatModel.builder().baseUrl(baseUrl).apiKey(apiKey).modelName("gpt-4").build();

        TokenStreamAssistant tokenStreamAssistant = AiServices.create(TokenStreamAssistant.class, streamingChatModel);
        TokenStream resTokenStream = tokenStreamAssistant.chat("今天天气怎么样");

        resTokenStream.onPartialResponse((String partialResponse) -> System.out.println(partialResponse))
                .onRetrieved((List<Content> contents) -> System.out.println(contents))
                .onToolExecuted((ToolExecution toolExecution) -> System.out.println(toolExecution))
                .onCompleteResponse((ChatResponse response) -> System.out.println(response))
                .onError((Throwable error) -> error.printStackTrace())
                .start();

        Thread.sleep(50000L);
    }
}

interface Assistant {
//    @SystemMessage("anser using chinese")
    String chat(String userMessage);

    Result<List<String>> generateOutline(String userMessage);
}

interface TokenStreamAssistant {
    TokenStream chat(String userMessage);
}
