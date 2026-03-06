package cn.yushijie.test;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolExecution;

import java.util.List;

/**
 * @ClassName ToolChatAIService
 * @Description function 调用
 * @Author yu155
 * @Date 2026/3/4 15:35
 * @Version 1.0.0
 */
public class ToolChatAIService {
    public static void main(String[] args) {
        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("gpt-4")
                .build();

        Assistant assistant = AiServices.builder(Assistant.class).chatModel(model).tools(new Tools()).build();
        Result<List<String>> result = assistant.generateOutline("475695037565 的平方根是多少");

        List<String> outline = result.content();
        TokenUsage tokenUsage = result.tokenUsage();
        List<Content> sources = result.sources();
        List<ToolExecution> toolExecutions = result.toolExecutions();
        FinishReason finishReason = result.finishReason();

        System.out.println(outline);
        System.out.println(tokenUsage);
        System.out.println(sources);
        System.out.println(toolExecutions);
        System.out.println(finishReason);
    }
}

class Tools {

    @Tool(value = "对给定的两个数求和")
    int add(int a, int b) {
        return a + b;
    }

    @Tool("返回给定数字的平方根")
    double squareRoot(double x) {
        return Math.sqrt(x);
    }

}
