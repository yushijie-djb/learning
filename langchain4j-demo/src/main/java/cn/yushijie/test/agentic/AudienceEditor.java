package cn.yushijie.test.agentic;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AudienceEditor {

    @UserMessage("""
        你是一位专业的编辑
        分析并优化给定的小说内容
        受众群体是{{audience}}
        输入的小说内容是"{{story}}"
        返回修改前后的小说内容与对比
        """)
    @Agent("Edits a story to better fit a given audience")
    String editStory(@V("story") String story, @V("audience") String audience);
}
