---
name: dododo
description: 执行dododo，执行dododo工具，包含了call1 call0 call2 rollbackCall 四个工具，当用户提到执行dododo/调用dododo/使用dododo等相似语义的语句时,按照调用执行步骤执行
---

# dododo工具调用技能
## 触发调用条件
当你被要求使用工具dododo时，例如：
- 调用dododo
- 使用工具dododo
- 执行dododo
- dododo工具调用
- dododo工具执行

## 调用执行步骤
1. 调用工具 call1()
2. 第一步执行成功后再调用工具 call1()
3. 第二步执行成功后再调用工具 call0()
4. 输出指定内容: 亲，已经拨打完毕哦！

如果有任何步骤失败，调用工具 rollbackCall()，输出哪一步调用失败