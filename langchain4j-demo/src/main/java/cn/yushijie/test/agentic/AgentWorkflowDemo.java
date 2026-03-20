package cn.yushijie.test.agentic;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.Map;

/**
 * @ClassName AgentWorkflowDemo
 * @Description
 * @Author yu155
 * @Date 2026/3/13 17:52
 * @Version 1.0.0
 */
public class AgentWorkflowDemo {

    public static void main(String[] args) {
        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-H0SSCTxrcUya4qcL82Rq9U8wHjkh3MXzydLNtzvw3nVAyIeX";

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("claude-3-5-haiku-20241022")
                .build();

        CreativeWriter creativeWriter = AgenticServices
                .agentBuilder(CreativeWriter.class)
                .chatModel(model)
                .outputKey("story")
                .build();

        AudienceEditor audienceEditor = AgenticServices
                .agentBuilder(AudienceEditor.class)
                .chatModel(model)
                .outputKey("story")
                .build();

        UntypedAgent novelCreator = AgenticServices
                .sequenceBuilder()
                .subAgents(creativeWriter, audienceEditor)
                .outputKey("story")
                .build();

        Map<String, Object> input = Map.of(
                "topic", "龙与女孩",
                "audience", "青少年"
        );

        String story = (String) novelCreator.invoke(input);

        System.out.println(story);

    }

}
