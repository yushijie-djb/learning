package cn.yushijie.test.agentic;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CreativeWriter {
    @UserMessage("""
            你是一个富有创意的小说作家
            依据给定的主题创作不超过50字的短篇小说
            只返回小说内容，不要附加其他信息
            要求的主题是{{topic}}.
            """)
    @Agent(outputKey = "story", description = "基于给定的主题写小说")
    String generateStory(@V("topic") String topic);
}
