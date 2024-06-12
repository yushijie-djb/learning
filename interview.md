# 百科全书

## Java

### 对象的创建方式

1. new关键字

2. 类名.class.newInstance() --public的无参构造器

3. ```java
   // Constructor.newInstance()
   // 包括public的和非public的，当然也包括private的
   Constructor<?>[] declaredConstructors = Person.class.getDeclaredConstructors();
   // 只返回public的~~~~~~(返回结果是上面的子集)
   Constructor<?>[] constructors = Person.class.getConstructors();
   
   
   Constructor<?> noArgsConstructor = declaredConstructors[0];
   Constructor<?> haveArgsConstructor = declaredConstructors[1];
   
   noArgsConstructor.setAccessible(true); // 非public的构造必须设置true才能用于创建实例
   Object person1 = noArgsConstructor.newInstance();
   Object person2 = declaredConstructors[1].newInstance("fsx", 18);
   ```

4. Object.clone()

5. ```java
   // 序列化
   Person person = new Person("fsx", 18);
   byte[] bytes = SerializationUtils.serialize(person);
   
   // 字节数组：可以来自网络、可以来自文件（本处直接本地模拟）
   Object deserPerson = SerializationUtils.deserialize(bytes);
   ```

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

#### 内存溢出/内存泄漏

​	内存溢出：程序所需内存大小超过当前所分配的内存范围

​	内存泄漏：已分配的内存无法回收造成速度减慢直至内存溢出

#### 内存结构

​	线程共享：

​		堆：对象、数组、字符串常量池、静态变量

​		方法区**<MetaSpace>**：类型信息 常量池表 运行时常量池

​		直接内存

​	线程私有：

​		虚拟机栈：栈帧

​		程序计数器

​		本地方法栈

#### 引用类型

​	强引用：

​		一般把一个对象赋给一个引用变量，这个引用变量就是强引用 Object o = new Object();

​		**垃圾回收器不会回收强引用对象，当内存空间不足时，JVM 抛出 OutOfMemoryError异常。**

​	软引用：

​		String str=new String("abc");                   // 强引用 

​		SoftReference<String> softRef=new SoftReference<String>(str);   // 软引用

​		**软化强引用 当内存空间不足的时候JVM对其进行回收**

​	弱引用：

​		MikeChen mikechen = new MikeChen()*;* 

​		WeakReference<MikeChen> wr = new WeakReference<MikeChen>(mikechen )*;*

​		**不管内存是否足够，只要发生 GC，都会被回收**

​	虚引用：

​		**幽灵引用暂无确切应用场景**

## Mysql

### 脏读、幻读、不可重复读

1. 脏读：读取到其他事务未提交的数据
2. 幻读：读取的数据增加/减少了
3. 不可重复读：读取的数据变了

### MVCC

### 锁

1. 意向锁：意向锁之间不互斥
2. 间隙锁：RR隔离级别下才有

### 锁语句

1. select...for update 加的是排他锁 

```java
select * from user_info where user_name ='杰伦' for update
// user_name是唯一索引
// 此处一共加了三把锁 table user_info IX | Record X idx_user_name | Record X primary
// Record X primary ? 防止其他事务通过主键修改值
```

2. 未使用索引的情况下 不一定锁全表

## Redis

### 热点key

1. 概念：大量的请求落在少数几个热点Key上导致Key所在的Redis节点压力陡增，影响其性能和稳定性甚至宕机。
2. 监控：redis-cli monitor/redis-cli slowlog/redis-cli -hotkeys
3. 解决：a. 熔断降级 b. 增加二级缓存(JVM缓存)，通过集群负载均衡来缓解Redis压力，首先查询二级缓存，没有再查一级缓存后更新到二级缓存，来分散请求压力。

### 大key

1.  概念：key对应的value过大 典型的如Hash zset
2.  监控：redis-cli --bigkeys （找大KEY）| DEBUG OBJECT key（分析大KEY）
3.  解决：业务上拆分为多个小KEY



## 分布式

### CAP

Consistency（一致性）、 Availability（可用性）、Partition tolerance（分区容错性）三者取其二。

一致性和可用性的矛盾：如果要保持server1和server2的一致性，那么server1在写数据时server2同时也不能对外提供读写服务，server1数据写完并且同步到server2后 server2才能开放服务，这与可用性相冲突。

### 分布式锁

#### 特征

1. 跨进程的锁住共享资源
2. 高可用的加锁和释放锁
3. 高性能的加锁和释放锁
4. 可重入
5. 锁失效机制
6. 阻塞/非阻塞特性
7. 锁续签机制

#### 实现方式

Mysql：

​	**创建一张“锁表” ，列应当具有唯一性**

​	**插入数据成功即是获取锁成功，释放锁时删除此数据**

​	*可重入性实现复杂*

​	*性能是数据库的瓶颈*

​	*锁失效机制难以保证*

​	*不具备非阻塞特性*

Zookeeper：

​	**依赖其临时顺序节点特性**

```java
1) 创建锁目录zk_lock
2) 尝试加锁的线程在zk_lock下创建临时顺序节点
3) 获取zk_lock下的子节点 判断自己是否是最小，如果是最小的 获取锁成功
4) 如果不是最小的 那么监听上一个节点 上一个节点被删除后再次加锁
5) 释放锁时删除临时有序节点
```

![image-20240607140341994](C:\Users\yu155\AppData\Roaming\Typora\typora-user-images\image-20240607140341994.png)

Redis：

​	**借助其SETNX EX | PX 特性结合LUA脚本来原子性的加锁释放锁**

