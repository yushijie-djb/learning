# 百科全书

## 时间管理预估

- 理想天数：团队成员根据理想天数给出他们的估计，这意味着他们想象他们在完成任务时不会受到任何干扰或分心。
- 斐波那契数列：在提供估计时，团队成员仅使用属于斐波那契数列（或其变体）的数字。
- 安全环境：建立一个安全的环境至关重要，让所有团队成员都可以轻松地表达他们的估计及其背后的原因。

## OS

### pageCache

app 应用程序和硬件之间隔着一个内核，内核通过 pagecache 来维护数据，若 pagecache 数据被标识为 `dirty`(脏页)，就会有一个 flush 刷新的过程，刷写到磁盘中去.

Linux 以页作为高速缓存的单位，当进程修改了高速缓存中的数据时，该页就会被内核标记为脏页，内核会在合适的时间把脏页的数据刷写到磁盘中去，以保持高速缓存中的数据与磁盘中的数据是一致的

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

#### 对象的内存布局(64位)

##### 对象头(Header)

###### Mark Word

占8字节，存储的数据会随着锁标志位的变化而变化

| 锁状态   | 56bit                                      | 1bit     | 4bit         | 1bit(偏向锁?) | 2bit(锁标志位) |
| -------- | ------------------------------------------ | -------- | ------------ | ------------- | -------------- |
| 无锁     | unused(25bit)\|hashCode(31bit)             | cms_free | 对象分代年龄 | 0             | 01             |
| 偏向锁   | threadId(偏向锁线程ID)(54bit)\|Epoch(2bit) | cms_free | 对象分代年龄 | 1             | 01             |
| 轻量级锁 | 指向栈中                                   | 锁的     | 记录的       | 指针(62bit)   | 00             |
| 重量级锁 | 指向                                       | 重量级   | 锁的         | 指针(62bit)   | 10             |
| GC标志   | CMS过程中                                  | 用到     | 的标记       | 信息(62bit)   | 11             |

###### Class Pointer

占8字节 

##### 实例数据(Instance data)

##### 对齐填充(Padding)

当一个线程进入一个对象的Synchronized方法后，其他线程是否可以进入此对象的其他方法

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

#### 垃圾回收算法

##### 判断为垃圾

##### 如何回收

#### 分代回收

​	为什么：不同对象的生命周期是不一样的，针对不同生命周期的对象使用不同的回收方式来提升效率

​	代划分：

​		年轻代：朝生夕死 Eden:from survivor:to survivor = 8:1:1 默认比例 HotSpot采用的复制算法来回收

​		年老代：

​		持久代：

​	Minor GC(Young GC)

​	Major GC(Full GC)

#### 垃圾收集器

##### 	CMS收集器

​		设计初衷：以获取最短回收停顿时间为目标

​		回收步骤：1. 初始标记 （STW）2. 并发标记 3. 重新标记（STW） 4. 并发清除

​		不足之处：1. 垃圾碎片 2. STW时间长 3. **concurrent mode failure ** 4. **promotion failed**

##### 	G1收集器

### 多线程

#### 读写锁

我在修改的时候,你的读操作阻塞等待

你在读的时候,我的修改操作阻塞等待

#### 乐观锁\悲观锁

- 乐观锁
  - 对共享变量的操作大部分场景下都不会冲突
  - 基于版本号：在每个数据记录中添加一个版本号字段，每当该记录被修改时，版本号就会增加1。在提交数据时，检查当前版本号是否与修改前的版本号相同，如果相同，则表示该记录未被其他用户修改过，可以提交数据；如果不同，则表示该记录已被其他用户修改过，需要重新操作。
  - 基于时间戳：在每个数据记录中添加一个时间戳字段，每当该记录被修改时，时间戳就会更新为当前时间。在提交数据时，检查当前时间戳是否与修改前的时间戳相同，如果相同，则表示该记录未被其他用户修改过，可以提交数据；如果不同，则表示该记录已被其他用户修改过，需要重新操作。
- 悲观锁
  - 对共享变量的操作大部分场景下都会冲突
  - 基于数据库锁
  - 基于程序锁

## Mysql

### Redo\Undo\Bin

详细文档:[大厂基本功 | MySQL 三大日志 ( binlog、redo log 和 undo log ) 的作用？ - 知乎](https://zhuanlan.zhihu.com/p/609972086)

#### RedoLog

1. 崩溃恢复能力 (Innodb存储引擎层面) 因此是在事务执行过程中持续写入
2. 日志文件组 环形数组 周而复始的写入
3. 为什么需要redolog而不是直接刷盘来避免数据丢失? 为了并发性能,mysql数据页大小16K数据页满刷盘,占用顺序存储空间,比每次随机刷盘(即使只有几十Byte)效率要高不少

#### UndoLog

1. 事务原子性 因此在事务执行前写入

#### BinLog

1. 数据备份\主从\多主数据同步(MySql Server层面) 因此是在事务提交时写入
1. 每次提交事务的时候将binglogCache写入文件系统缓存中,然后由内核判断什么时候刷盘

#### 两阶段提交

- redolog和binlog记录的数据不一致怎么办? 
- 当写入Binlog异常时RedoLog处于prepare阶段事务失败
- 当redoLog处于commit阶段失败时,由于binlog写入正常 此时事务不会失败

![img](https://pica.zhimg.com/v2-c9357ad4f60c3208d1a78a46b1318b9e_b.jpg)

### 脏读、幻读、不可重复读

1. 脏读：读取到其他事务未提交但已经修改的数据
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

### 多主集群

**Galera 是一个多主复制集群**，事务会被同步地在所有节点上传播。当 A 节点执行 `FOR UPDATE` 锁定某行时，事务会被复制到 B 和 C 节点。由于 Galera 集群的一致性协议，所有节点上的操作必须保持一致。

**事务的锁会在所有节点间共享**，这意味着如果一个事务在 A 节点获得了行级锁，其他节点（如 B 节点）也不能修改该行，除非 A 节点提交或回滚事务。

## Redis

### 热点key

1. 概念：大量的请求落在少数几个热点Key上导致Key所在的Redis节点压力陡增，影响其性能和稳定性甚至宕机。
2. 监控：redis-cli monitor/redis-cli slowlog/redis-cli -hotkeys
3. 解决：a. 熔断降级 b. 增加二级缓存(JVM缓存)，通过集群负载均衡来缓解Redis压力，首先查询二级缓存，没有再查一级缓存后更新到二级缓存，来分散请求压力。

### 大key

1.  概念：key对应的value过大 典型的如Hash zset
2.  监控：redis-cli --bigkeys （找大KEY）| DEBUG OBJECT key（分析大KEY）
3.  解决：业务上拆分为多个小KEY

### 内存淘汰策略

1. 只淘汰配置了TTL的Key

   - volatile-LRU(Least Recently Used) [只要被访问一次就认为是热点数据 短时间内不会被淘汰]

   - volatile-LFU(Least Frequently Used)[依据Key最近的访问频率来判断]

   - volatile-random[随机删除，适用于分布均衡的场景]

2. 淘汰所有Key

   - allkeys-LRU
   - allkeys-LFU
   - allkeys-random

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

## Spring

### AMQP

#### @RabbitListener

每一个方法都是独自的Container

#### container

##### SimpleMessageListenerContainer

SMLC为每个消费者使用一个内部队列和一个专用线程。如果一个容器被配置为监听多个队列，同一个消费者线程被用来处理所有的队列(也就是同一个Channel)

##### DirectMessageListenerContainer
