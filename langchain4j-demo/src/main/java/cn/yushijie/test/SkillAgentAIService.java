package cn.yushijie.test;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.skills.FileSystemSkillLoader;
import dev.langchain4j.skills.Skills;

import java.nio.file.Path;
import java.time.Duration;

/**
 * @ClassName SkillAgentAIService
 * @Description
 * @Author yu155
 * @Date 2026/3/11 16:17
 * @Version 1.0.0
 */
public class SkillAgentAIService {

    public static void main(String[] args) {
        String baseUrl = "http://10.0.1.253:11434";
        String apiKey = "ollama";

        ChatModel model = OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName("qwen3.5:9b")
                .logRequests(true)
                .timeout(Duration.ofMinutes(10L))
                .build();

        Skills skills = Skills.from(FileSystemSkillLoader.loadSkills(
                Path.of("D:\\develop\\idea\\Project\\github\\learning\\langchain4j-demo\\src\\main\\resources\\skill")));

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new SkillTool())
                .toolProvider(skills.toolProvider())
//                .systemMessage("你是一个智能助手，可以使用以下技能" + skills.formatAvailableSkills())
                .build();

        String chat = assistant.chat("执行dododo");
        System.out.println(chat);
    }

}

class SkillTool {

    @Tool(name = "call1")
    public void call1() {
        System.out.println(1);
    }

    @Tool(name = "call0")
    public void call0() {
        System.out.println(0);
    }

    @Tool(name = "call2")
    public void call2() {
        System.out.println(2);
    }

    @Tool(name = "rollbackCall")
    public void rollbackCall() {
        System.out.println("rollback call");
    }

}


