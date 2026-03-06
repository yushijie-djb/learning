# AI Agent

**核心流程**：<font color=red>**感知-思考-行动-观察**</font> （循环往复）

![](.\img\agent&llm.png)

# ReAct

**ReAct** = **Re**asoning + **Act**ing

- **推理(Reasoning)**：让模型进行内部思考和逻辑推理
- **行动(Acting)**：基于推理结果执行外部动作（如调用工具、API等）

它是一种**增强语言模型推理能力**的框架，让LLM能够像人类一样：**先思考，再行动**

```python
问题 → 思考(推理) → 行动 → 观察结果 → 再思考 → 再行动 → ... → 最终答案
```

*关键词：*

- **Thought**：模型的内部推理，解释“为什么”要采取某个行动
- **Action**：具体的外部操作（如搜索、计算、查询）
- **Observation**：行动返回的结果/观察

# Promot

# RAG

## 三大痛点

1. **知识局限性**：突破训练数据时间截断（如GPT-4的2023年4月截止）
2. **幻觉抑制**：通过事实性检索结果约束生成内容
3. **领域适应**：无需微调即可接入专业数据

## 工作流程

### 通用流程

<img src=".\img\rag工作流程.svg" height = 500px />



### LangChain4j

```java
            User Query
                │
                ▼
           Query Router
                │
      ┌─────────┴─────────┐
      ▼                   ▼
Content Retriever A   Content Retriever B
      │
      ▼
  Embedding
      │
      ▼
 Vector Store
      │
      ▼
   Content
      │
      ▼
     LLM
      │
      ▼
    Answer
```
### 工具调用

```java
### Tool: get_weather
Description: 获取指定城市的当前天气。
When to use:
  - 用户询问“天气”、“气温”、“会不会下雨”等。
  - 用户提到具体城市名且意图为查询实时天气。
Parameters:
  - city (string, required): 城市名称，如“北京”、“上海”。
Example:
  User: 北京今天热吗？
  Assistant: (调用 get_weather(city="北京") ) 当前北京气温22℃，多云。
```

# Tool

# MCP

# Multi-Agent

# LangChain4j

```java
// 依赖导入
<dependencies>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-open-ai</artifactId>
            <version>1.10.0</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j</artifactId>
            <version>1.10.0</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-bom</artifactId>
            <version>1.10.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
</dependencies>
```

## startChat

```java
String baseUrl = "https://yinli.one/v1";
String apiKey = "sk-RDAxCPj1hnXMBucOHBdLVTEuI1AxxRQIkOwmmodc6DHitMIo";

OpenAiChatModel model = OpenAiChatModel.builder()
    .baseUrl(baseUrl)
    .apiKey(apiKey)
    .modelName("gpt-4")
    .build();

ChatRequest request = ChatRequest.builder()
    .messages(UserMessage.from("say 'hello world'"))
    .parameters(ChatRequestParameters.builder()
                .temperature(0.5)
                .build())
    .build();

ChatResponse chatResponse = model.chat(request);
// 简化调用
//        String answer = model.chat("say 'hello world'");
System.out.println(chatResponse); // hello world
```

## chatMemory

### memory和history

*memory（记忆）不等于历史*

- 历史是所有对话的列表，完整无缺，是用户UI界面看到的内容
- 记忆保存一些呈现给LLM的信息，根据不同的记忆算法他可以以各种方式修改历史：淘汰部分历史、抽取总结部分历史、抽取总结单独的消息、向消息中注入额外信息等
- **langchain4j不提供历史保存只提供记忆**

### 淘汰策略

- MessageWindowChatMemory作为滑动窗口运行， 保留最近的`N`条消息，并淘汰不再适合的旧消息。 
- TokenWindowChatMemory它也作为滑动窗口运行，但专注于保留最近的N个令牌， 根据需要淘汰旧消息。 消息是不可分割的。如果一条消息不适合，它会被完全淘汰。 TokenWindowChatMemory需要一个Tokenizer来计算每个ChatMessage中的令牌数。

### 记忆持久化

- 默认情况下ChatMemory默认实现在内存中的存储
- 自定义

```java
class PersistentChatMemoryStore implements ChatMemoryStore {

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
          // TODO: 实现通过内存ID从持久化存储中获取所有消息。
          // 可以使用ChatMessageDeserializer.messageFromJson(String)和
          // ChatMessageDeserializer.messagesFromJson(String)辅助方法
          // 轻松地从JSON反序列化聊天消息。
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            // TODO: 实现通过内存ID更新持久化存储中的所有消息。
            // 可以使用ChatMessageSerializer.messageToJson(ChatMessage)和
            // ChatMessageSerializer.messagesToJson(List<ChatMessage>)辅助方法
            // 轻松地将聊天消息序列化为JSON。
        }

        @Override
        public void deleteMessages(Object memoryId) {
          // TODO: 实现通过内存ID删除持久化存储中的所有消息。
        }
}
```

- systemMessage处理：一旦添加systemMessage总是被保留，只有一条systemMessage，新添加的会替换（忽略相同）























