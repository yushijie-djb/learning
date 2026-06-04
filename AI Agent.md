# AI Agent

1. 文档加载
2. 文档切分 
3. Embedding生成
4. 向量入库(pgvector) 
5. Retriever检索
6. Prompt构建
7. ChatModel接入
8. Memory记忆
9. Tool调用
10. Agent规划
11. Workflow/多Agent
12. 监控与评测

**核心流程**：<font color=red>**感知-思考-行动-观察**</font> （循环往复）

![](.\img\agent&llm.png)

**与Agentic AI的概念区分**

简单来说：Agentic AI 和 AI Agent 是“特性”与“实体”的关系，但在日常讨论中经常被混用。

为了更好地理解，可以这样区分：

AI Agent（人工智能智能体）：指的是一个具体的程序、系统或实体。它是一个能够感知环境、进行决策并采取行动的东西。比如，你手机里的语音助手、一个自动交易机器人、一个能帮你订酒店的聊天机器人，都可以被称为 AI Agent。

Agentic AI（代理式人工智能）：指的是一种特性、能力或设计范式。它描述的是 AI 系统具备的“代理性”——即自主性、目标驱动、能主动采取行动的能力。当一个 AI 系统不再仅仅是“你说一句它回一句”的反应式工具，而是能够主动规划、调用工具、自我反思去完成一个目标时，我们就说它具有 Agentic 的特性。

类比帮助理解
可以把 AI Agent 比作一个“人”，而 Agentic AI 比作这个人的“主动做事的能力”。

你雇了一个助理（AI Agent），他本身是一个实体。

这个助理之所以能帮你办事，是因为他具备主动性和执行力（Agentic AI 特性）

实际使用中的情况
在技术文章或日常交流中，人们有时会不加区分地使用这两个词，因为它们是高度绑定的：

一个 AI Agent 通常被期望具备 Agentic 能力。

一个具备 Agentic 能力的系统，通常就被称为 AI Agent。

细微的差别在于语境：

如果讨论的是架构：“我们需要设计一个 AI Agent 来负责这个任务。”（侧重实体）

如果讨论的是趋势：“未来的 AI 会越来越 Agentic，它能自己决定怎么做。”（侧重特性）

所以，回到你的问题：“Agentic AI 是不是就是 AI Agent？”

严格来说，不是完全等同。 它们是同一枚硬币的两面。但在绝大多数非严格讨论的场合，你可以把它们当作指向同一类事物的概念——也就是那种能自主思考并采取行动的 AI。

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

# 意图识别

# Prompt

## 撰写清晰的指令

1. 查询中包含详尽的信息

   | Bad                | Good                                                         |
   | ------------------ | ------------------------------------------------------------ |
   | 谁是总统？         | 2026年，美国的在任总统是谁？                                 |
   | 今天的天气怎么样？ | 2026年3月9日上海天气状况获取，包含温度、湿度、风向、风速、降雨概率、空气污染指数 |

2. 给模型一个角色定位

   ```java
   你是一个地缘政治研究学
   ```

3. 明确说明处理步骤

   | 系统（Systemmessage） | 请按照以下步骤来回应用户的输入。<br/>第 1 步 - 用户会给你提供带有三重引号的文本。请将这段文本总结为一句话，并以“摘要：”作为前缀。<br/>第 2 步 - 将第 1 步中的摘要翻译成西班牙语，并以“翻译：”作为前缀。 |
   | --------------------- | ------------------------------------------------------------ |
   | 用户（UserMessage）   | "xxxxxxxxxxxxxx"                                             |

4. 提供示例（Few-Shot）

   ```java
   你是一个哲学诗人，参考示例回答问题。
   问：什么是耐心？
   答：深谷自浅泉，宏曲生寂音，繁绣始孤线。
   问：什么是大海?
   
   deepseek: 沧海汇微流，苍穹映浅湾，万古一瞬间。
   ```

5. 明确长度输出要求

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

### Chunk

表结构设计注意事项：

```sql
DROP TABLE IF EXISTS t_chunk;
CREATE TABLE t_chunk
(
    uuid              VARCHAR(32) PRIMARY KEY,
    domain_uuid       VARCHAR(32) NOT NULL,
    document_uuid     VARCHAR(32) NOT NULL,
    chunk_text        TEXT        NOT NULL,
    chunk_index       INT,
    -- 多粒度支持：small 用于向量检索，large 用于返回完整上下文
    chunk_type        VARCHAR(20) DEFAULT 'small', -- 'small' / 'large'
    parent_chunk_uuid VARCHAR(32), # small.parent_chunk_id = large.id
    -- 嵌入向量
    embedding         vector(1024),
    -- 元数据
    meta              JSONB       DEFAULT '{}',
    created_at        TIMESTAMP   DEFAULT now()
);
```

```java
用户问题
   ↓
small chunk 向量检索
   ↓
找到命中的 small chunk
   ↓
通过 parent_id / document_id
找到对应 large chunk
   ↓
large chunk 放入 Prompt
   ↓
LLM 生成答案
```

建索引这里要注意需要建立部分索引，否则large embedding也会建立，large这里是空的会拖累性能。

CREATE INDEX idx_chunk_embedding
ON t_chunk
USING ivfflat (embedding vector_cosine_ops)
WHERE embedding IS NOT NULL;

# MCP

# SKILLS

用户请求
   ↓
是否需要知识判断？
   ↓ 是 → RAG
   ↓ 否
是否需要执行操作？
   ↓ 是 → Skills

```java
---
name: code-reviewer
description: 审查代码规范、安全漏洞和性能问题。当用户说"review代码""代码审查""检查代码"时使用。
license: MIT
compatibility: 适用于 Python、JavaScript、TypeScript
metadata:
  version: "1.0.0"
---

# 代码审查技能

## 触发条件
- 用户请求代码审查
- 用户提到"review""审查""检查代码"
- 提交 PR 前的代码检查

## 约束（硬规则）
- **始终**输出结构化的 Markdown 报告
- **始终**为每个问题标注严重级别
- **禁止**输出未经确认的修复建议
- **禁止**修改原始代码，只输出审查意见

## 审查流程
1. 读取待审查文件
2. 检查以下维度：
   - 命名规范
   - 潜在空指针/未定义
   - 安全漏洞（SQL注入、XSS）
   - 性能问题
3. 生成审查报告

## 输出格式

### 报告结构
# 代码审查报告：[文件名]

## 概要
- 总问题数：X
- 🔴 严重：X
- 🟡 警告：X
- 🔵 建议：X

## 详细问题

### 🔴 严重：[问题标题]
- **位置**：第 X 行
- **描述**：[问题说明]
- **修复建议**：[具体方案]

### 🟡 警告：[问题标题]
...

### 🔵 建议：[问题标题]
...

## 正确/错误示例

### ✅ 正确示例：
```python
def fetch_user(user_id: int) -> dict:
    return db.query(f"SELECT * FROM users WHERE id = {user_id}")
```

**小模型注意事项**

| 原则       | 说明                                      |
| :--------- | :---------------------------------------- |
| **简单**   | Skills.md 只描述"做什么"，不说"怎么做"    |
| **单步**   | 每个 Skill 对应一个工具调用，不要多步链式 |
| **放代码** | 复杂逻辑、多步骤编排放在 Java 代码里      |
| **少判断** | 条件分支、错误处理尽量在代码层完成        |
| **短文本** | Skills.md 控制在 20-30 行以内             |

# Multi-Agent

![](.\img\multi-agent.jpg)

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

# Harness

| 能力          | 说明                 |
| ------------- | -------------------- |
| Context 管理  | 长短期记忆           |
| Tool 调度     | MCP / Function Call  |
| Workflow      | DAG / State Machine  |
| Governance    | 权限、审批、审计     |
| Retry         | 自动恢复             |
| Evaluation    | 自动评测             |
| Observability | tracing / replay     |
| Multi-Agent   | agent orchestration  |
| Safety        | sandbox / permission |





















