# 面试题大全

## Java

### 数据结构

#### PriorityQueue

二叉堆: 

​	最大堆-**父结点的键值总是大于或等于任何一个子节点的键值** 最小堆-**父结点的键值总是小于或等于任何一个子节点的键值（<u>*此处是最小堆*</u>）**

​	规范：堆总是一棵完全树(第n层深度被填满之前，不会开始填第n+1层深度，而且元素插入是从左往右填满。基于这个特点，二叉堆又可以用数组来表示而不是用链表)

​	ShiftUp保证添加节点时符合二叉堆规范

​	[PriorityQueue源码分析 - linghu_java - 博客园 (cnblogs.com)](https://www.cnblogs.com/linghu-java/p/9467805.html)

### transient

修饰的成员属性变量不被序列化

### JVM

#### 内存结构

​	线程共享：

​		堆：对象、数组、字符串常量池、静态变量

​		方法区**<MetaSpace>**：类型信息 常量池表 运行时常量池

​		直接内存

​	线程私有：

​		虚拟机栈：栈帧

​		程序计数器

​		本地方法栈

## Mysql

## Redis

### 热点key

1. 概念：大量的请求落在少数几个热点Key上导致Key所在的Redis节点压力陡增，影响其性能和稳定性甚至宕机。
2. 监控：redis-cli monitor/redis-cli slowlog/redis-cli -hotkeys
3. 解决：a. 熔断降级 b. 增加二级缓存(JVM缓存)，通过集群负载均衡来缓解Redis压力，首先查询二级缓存，没有再查一级缓存后更新到二级缓存，来分散请求压力。

### 大key

1.  概念：key对应的value过大 典型的如Hash zset
2.  监控：redis-cli --bigkeys （找大KEY）| DEBUG OBJECT key（分析大KEY）
3.  解决：业务上拆分为多个小KEY