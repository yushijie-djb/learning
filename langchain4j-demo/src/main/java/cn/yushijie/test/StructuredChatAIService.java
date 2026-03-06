package cn.yushijie.test;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.LocalDate;

/**
 * @ClassName StructuredChatAIService
 * @Description 结构化输出
 * @Author yu155
 * @Date 2026/3/4 14:22
 * @Version 1.0.0
 */
public class StructuredChatAIService {

    public static void main(String[] args) {
        String baseUrl = "https://yinli.one/v1";
        String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("gpt-4")
                .build();

        PojoAssistant pojoAssistant = AiServices.create(PojoAssistant.class, model);
        String promot = """
                In 1998, A big thing happened, Shijie Yu was born in china, It was May 24, A Sunny Day, The world will change because of the boy!
                """;
        Person person = pojoAssistant.extractPersonFrom(promot);
        System.out.println(person);
    }

}

interface BooleanAssistant {

    @UserMessage("does {{message}} has a positive sentiment")
    boolean isPositiveSentiment(String message);

}

class Person {
    // 您可以添加可选描述，帮助 LLM 更好地理解
    @Description("first name of a person")
    String firstName;
    String lastName;
    LocalDate birthDate;

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}

interface PojoAssistant {

    @UserMessage("Extract information about a person from {{text}}")// 使用@V显示绑定参数 或者用默认的it来绑定第一个
    Person extractPersonFrom(@V("text") String text);

}
