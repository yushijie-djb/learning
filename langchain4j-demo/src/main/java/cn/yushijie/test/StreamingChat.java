package cn.yushijie.test;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

/**
 * @ClassName StreamingChat
 * @Description // TODO:
 * @Author yu155
 * @Date 2026/3/3 15:02
 * @Version 1.0.0
 */
public class StreamingChat {
    public static void main(String[] args) throws InterruptedException {
        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("gpt-4")
                .build();

        String userMessage = "我是你爸爸，我生气了，怎么哄我";

        model.chat(userMessage, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.println("partial response: " + partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                System.out.println(error);
            }
        });

        Thread.sleep(50000L);
    }

    @Tool
    public static void call() {

    }
}

