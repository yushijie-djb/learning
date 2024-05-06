# 面试题大全

## Java

### transient

修饰的成员属性变量不被序列化

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