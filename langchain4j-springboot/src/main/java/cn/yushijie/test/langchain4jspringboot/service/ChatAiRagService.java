package cn.yushijie.test.langchain4jspringboot.service;

import dev.langchain4j.service.spring.AiService;

@AiService
public interface ChatAiRagService {
    String chat(String msg);
}
