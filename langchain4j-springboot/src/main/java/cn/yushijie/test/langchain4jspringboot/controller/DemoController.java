package cn.yushijie.test.langchain4jspringboot.controller;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DemoController
 * @Description // TODO:
 * @Author yu155
 * @Date 2026/3/17 15:58
 * @Version 1.0.0
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    ChatModel chatModel;

    @GetMapping("/chat")
    public String chat(@RequestParam("userMsg") String userMsg) {
        return chatModel.chat(userMsg);
    }

    @GetMapping("/rag")
    public String rag(@RequestParam("userMsg") String userMsg) {

    }

}