# Agent

# ReAct

**ReAct** = **Re**asoning + **Act**ing

- **推理(Reasoning)**：让模型进行内部思考和逻辑推理
- **行动(Acting)**：基于推理结果执行外部动作（如调用工具、API等）

它是一种**增强语言模型推理能力**的框架，让LLM能够像人类一样：**先思考，再行动**。

```python
问题 → 思考(推理) → 行动 → 观察结果 → 再思考 → 再行动 → ... → 最终答案
```

*关键词：*

- **Thought**：模型的内部推理，解释“为什么”要采取某个行动
- **Action**：具体的外部操作（如搜索、计算、查询）
- **Observation**：行动返回的结果/观察

# Langchain

## Client

```python
client = OpenAI(
    # 下面两个参数的默认值来自环境变量，可以不加
    api_key=os.environ.get("OPENAI_API_KEY"),
    base_url=os.environ.get("OPENAI_BASE_URL"),
)
```

### Completions

#### create

Request: 

| 参数名      | 描述                                                         |
| ----------- | ------------------------------------------------------------ |
| model       | 模型类型，如GPT-4                                            |
| prompt      | 提示，即输入模型的指令                                       |
| temperature | 影响输出随机性的参数，一般0~2之间，值越高输出越随机，值越低输出越确定 |
| max_tokens  | 限制最大输出参数长度                                         |
| suffix      | 允许在输出文本后附加的后缀参数，默认Null                     |
| top_p       | 核心抽样参数，模型将只考虑概率质量最高的参数                 |
| n           | 决定每个提示生成多少个完整的输出                             |
| stream      | 决定是否实时流式传输生成Token                                |
| ..........  | <font color='red'>每个LLM供应商参数大同小异</font>           |

Response: 

| 参数名                          | 描述                                                         |
| ------------------------------- | ------------------------------------------------------------ |
| id                              | 对话的唯一标识符                                             |
| choices                         | 模型生成的 completion 的选择列表                             |
| choices.finish_reason           | 可能值: [stop, length, content_filter, tool_calls, insufficient_system_resource] 模型停止生成 token 的原因。 |
| choices.index                   | 该 completion 在模型生成的 completion 的选择列表中的索引     |
| choices.message                 | 模型生成的 completion 消息                                   |
| choices.message.content         | 该 completion 的内容                                         |
| choices.message.tool_calls      | 模型生成的 tool 调用，例如 function 调用                     |
| choices.message.tool_calls.id   | tool 调用的 ID                                               |
| choices.message.tool_calls.type | tool 的类型                                                  |
| choices.message.role            | 角色                                                         |
| choices.logprobs                | 该 choice 的对数概率信息                                     |
| created                         | 创建聊天完成时的 Unix 时间戳（以秒为单位）                   |
| model                           | 生成该 completion 的模型名                                   |
| usage                           | 该对话补全请求的用量信息                                     |
| usage.completion_tokens         | 模型 completion 产生的 token 数                              |
| usage.prompt_tokens             | 用户 prompt 所包含的 token 数                                |
| usage.prompt_cache_hit_tokens   | 用户 prompt 中，命中上下文缓存的 token 数                    |
| usage.prompt_cache_miss_tokens  | 用户 prompt 中，未命中上下文缓存的 token 数                  |
| usage.total_tokens              | 该请求中，所有 token 的数量                                  |

