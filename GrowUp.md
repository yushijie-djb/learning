# GROW UP

## 时间管理预估

- 理想天数：团队成员根据理想天数给出他们的估计，这意味着他们想象他们在完成任务时不会受到任何干扰或分心。
- 斐波那契数列：在提供估计时，团队成员仅使用属于斐波那契数列（或其变体）的数字。
- 安全环境：建立一个安全的环境至关重要，让所有团队成员都可以轻松地表达他们的估计及其背后的原因。

## OS

### pageCache

app 应用程序和硬件之间隔着一个内核，内核通过 pagecache 来维护数据，若 pagecache 数据被标识为 `dirty`(脏页)，就会有一个 flush 刷新的过程，刷写到磁盘中去.

Linux 以页作为高速缓存的单位，当进程修改了高速缓存中的数据时，该页就会被内核标记为脏页，内核会在合适的时间把脏页的数据刷写到磁盘中去，以保持高速缓存中的数据与磁盘中的数据是一致的

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

![](.\img\jvm_memory_construct.png)

#### JVM信息查询

jinfo <pid>

### 双亲委派机制

![](.\img\双亲委派模型.png)

JDBC打破双亲委派机制：首先明确JDBC（Java.sql）定义位于rt.jar中，这属于BootStrapClassLoader的加载范围。但是厂商提供的驱动实现都是放在我们自己的CLASSPATH下面，这超出了BootStrapClassLoader的加载范围（SPI接口中的代码经常需要加载具体的实现类。 并且我们知道一个类由类加载器A加载,那么这个类依赖类也应该由相同的类加载器加载.那么问题来了，BootStrapClassLoader无法找到 SPI 的实现类的，因为它只加载 Java 的核心库。）

#### 性能排查

##### CPU高

- 实时高
  - 确认进程PID
  - jstack导出进程堆栈文件
  - 分析堆栈文件（主要是RUNNABLE/BLOCKED/TIMED_WAITING）
  - 没有进展-考虑是否是频繁GC导致-jstat查看
  - 内存dump看哪个区有问题（jmap）
- 偶发高
  - 自动化监控+日志记录

##### 内存高

- 实时高
  - 确认进程PID
  - 观察JVM GC回收情况 jstat -gc <pid> 1000  # 每1秒输出一次GC统计
  - 生成heap dump文件 jmap -dump:live,format=b,file=heapdump.hprof <pid>
  - JProfile、VisualVM 分析hprof文件

- 偶发高
  - 自动化监控+日志记录


#### 对象的创建方式

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

为什么要对齐填充？-- 

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

#### 垃圾回收

##### 判断为垃圾

- **引用计数法**：
  - 一个对象, 如果没有引用指向, 那么这个对象以后一定无法被使用
  - 计数器需要占据额外的内存空间、存在循环引用问题
- **可达性分析**：
  - 通过一系列称为 “GC Roots” 的对象作为起始点，从这些节点开始向下搜索，搜索走过的路径称之为"引用链"，当一个对象到 GC Roots 没有任何的引用链相连时(从GC Roots到这个对象不可达)时，证明此对象是不可用的
  - GC Roots： **虚拟机栈中对象的引用**、**本地方法栈中 JNI 引用的对象**、**方法区中类静态属性对象的引用**、**方法区中常量引用的对象**
  - 存在STW停顿开销
  - *并发的可达性分析*：https://zhuanlan.zhihu.com/p/414078522

##### 如何回收

- **标记-清除**：首先标记出所有需要回收的对象，在标记完成后统一回收所有被标记的对象
  - *效率不高、存在内存碎片*（在申请内存时, 都是申请的连续内存空间, 释放内存, 就可能会破坏原有的连续性, 导致 “有内存, 但无法申请”）
- **标记-复制**：将可用内存按容量划分为大小相等的两块，每次只使用其中的一块，清理时将A 中存活下来的对象全部复制到 B 中, 把 A 中内存统一释放，依次循环
  - *内存空间利用率低、存活对象多时复制操作效率低*
- **标记-整理**：让所有存活对象都向一端移动，然后直接清理掉端边界以外的内存
  - 存活对象多时移动操作效率低

##### 分代回收

​	**为什么**：不同对象的生命周期是不一样的，针对不同生命周期的对象使用不同的回收方式来提升效率

​	**代划分**：

​		年轻代（Young 1/3）：朝生夕死 Eden:from-survivor:to-survivor = 8:1:1 

​		年老代（Old 2/3）：大对象和经历了 N 次（一般情况默认是 15 次）垃圾回收依然存活下来的对象

​		Minor GC（Young GC）：Young区的对象大都是朝生夕死，存活率低下，Eden存活对象->from区，from存活对象->to区

​		Major GC（Full GC）：Full GC往往伴随着一次Young GC，Full GC时间更久

##### OOPMAP

oopmap和safepoint二者是相互依存的，单独讲任何一个都是无意义的。

用于**枚举 GC Roots**，记录栈中引用数据类型的位置（空间换时间，避免全栈扫描）

##### SAFE POINT

oopmap和safepoint二者是相互依存的，单独讲任何一个都是无意义的。

OOPMAP记录了GC ROOTS信息，但是程序是在不断运作的，那么OOPMAP中的数据就要不断更新（成本太高），如果程序到达安全点才能暂停下来进行GC，那么只需要在程序进入安全点时更新OOPMAP。

安全点位置：

- 方法调用时
- 进入循环边界
- 异常抛出

Tips: Thread.sleep(0)的妙用，在超长循环中，可以自行设置Thread.sleep(0)来防止长时间无法进入安全点。

##### SAFE REGION

安全点需要程序自己跑过去，对于SLEEP和BLOCKED的线程，可能很难在短时间内到达安全点，这些线程已经不会导致引用关系变化，所处的代码区域就叫做安全区域，处于安全区域时，任何时间进行GC都没关系。

##### 三色标记算法

*黑色表示从 GCRoots 开始，已扫描过它全部引用的对象（存活对象）*

*灰色指的是扫描过对象本身，还没完全扫描过它全部引用的对象（待处理的中间状态）*

*白色指的是还没扫描过的对象（扫描结束后仍为白色就是不可达对象，需要回收）*

##### 垃圾收集器

###### 	CMS（**Concurrent Mark Sweep**）分代收集器--仅作用于老年代

​		**设计初衷**：以获取最短回收停顿时间为目标

​		**回收步骤**

​			初始标记: 标记 GCRoots 直接引用的节点为灰色（STW）

​			并发标记: 从灰色节点开始，去扫描整个引用链，然后将它们标记为黑色（最耗时） 

​			重新标记（STW） 

​			并发清除

​		**不足之处**：CPU资源敏感、标记清除算法导致内存碎片、浮动垃圾无法及时处理

###### 	G1分区收集器

​		**设计初衷**：最大限度降低停顿时间（可配置），增量回收方式大内存表现更好

​		**内存区域(Region)划分**：没有明确提前划分代和区域，Region大小可配置，每个Region都可以是Eden\Survivor\Old

![](img\G1内存结构.webp)

​		**回收流程**：

​				![](img\G1误回收场景.webp)

RegionB和RegionC中的对象由于**晋升或者移动**到RegionA，那么本身是被GCRoots引用的对象的内存地址发生了变化，如果没有一种机制来记录这种跨Region引用，那么对RegionA进行清理时会误清理原本在RegionB和RegionC中被GCRoots引用的对象。

**RememberedSet**：对于每一个Region，都有一个自己对应的RSet，那么在对象发生移动时将其记录下来，不仅仅从GCRoots开始扫描，也从RSet开始扫描。

### 多线程

#### 锁升级

*为什么需要*：synchronized阻塞或唤醒一个Java线程需要操作系统切换CPU状态来完成，这种状态切换需要耗费处理器时间，如果同步代码块中内容过于简单，这种切换的时间可能比用户代码执行的时间还长。

##### 无锁状态

当对象刚被创建出来，并且还没有任何线程尝试获取它的锁时，对象处于无锁状态。

##### 偏向锁

- **设计目标：** 优化**同一个线程**多次进入同步块，且**没有其他线程竞争**的场景。它假设在大多数情况下，锁不仅不存在竞争，而且总是由同一线程获得**（偏向于第一个获得它的线程）**。
- **升级触发条件：** 当第一个线程 `T1` 尝试获取该对象的锁时。
- **升级过程：**
  1. 检查锁标志位是否为 `01` (无锁/可偏向)。
  2. 通过 **CAS (Compare-And-Swap)** 操作，尝试将 Mark Word 中的 **Thread ID** 字段设置为当前线程 `T1` 的 ID。
  3. 同时将锁标志位设置为 `01`（偏向模式）。
  4. 如果 CAS 成功，线程 `T1` 获得偏向锁，进入同步块执行。
  5. 后续只要 `T1` 再次进入这个同步块，它只需要检查 Mark Word 中的 Thread ID 是否指向自己：
     - 如果是：直接进入同步块，**没有任何额外的 CAS 或锁操作开销**。
     - 如果不是：说明有其他线程尝试过获取锁（偏向锁被“质疑”），此时需要撤销偏向锁（Revoke Bias）。
- **撤销偏向锁：** 当另一个线程 `T2` 尝试获取这个已被 `T1` 偏向的锁时：
  - 需要等待全局安全点（Safepoint，此时 JVM 暂停所有线程）。
  - 检查持有偏向锁的线程 `T1` 是否还活着：
    - 如果 `T1` 已不存活或已退出同步块：将对象头置为无锁状态 (`01`)，然后 `T2` 可以尝试以偏向锁或轻量级锁的方式重新竞争。
    - 如果 `T1` 仍在同步块中执行：说明发生了竞争，需要升级为**轻量级锁**。将对象头设置为指向 `T1` 栈帧中锁记录（Lock Record）的指针（此时锁标志位变为 `00`），表示轻量级锁。`T2` 接下来会尝试通过 CAS 获取这个轻量级锁。
- **优点：** 同一个线程重入时开销极小（仅需一次 CAS 初始化，后续直接检查 Thread ID）。
- **缺点：** 存在撤销开销（尤其是在竞争场景下）。JDK 15 后默认关闭偏向锁（`-XX:-UseBiasedLocking`），因为现代应用很多是高并发、短生命周期的对象，偏向锁的收益变小而撤销成本相对突出。

##### 轻量级锁

- **设计目标：** 优化**多个线程交替执行**同步块，但**不存在同一时刻竞争**（即竞争非常轻微，线程不会阻塞）的场景。它基于“自旋锁”的思想。
- **升级触发条件：**
  - 关闭了偏向锁。
  - 偏向锁被撤销（因为出现了竞争线程）。
  - 尝试获取偏向锁时 CAS 设置 Thread ID 失败（说明已有其他线程偏向）。
- **加锁过程：**
  1. 在当前线程 `T` 的栈帧中创建一个名为 **Lock Record** 的空间。
  2. 将对象当前的 **Mark Word** 复制到线程 `T` 的 Lock Record 中（称为 Displaced Mark Word）。
  3. 使用 **CAS** 尝试将对象头的 Mark Word **更新为指向 `T` 栈帧中 Lock Record 的指针**。
  4. 如果 CAS 成功：
     - 锁标志位设置为 `00` (轻量级锁)。
     - 线程 `T` 成功获得轻量级锁，进入同步块执行。
  5. 如果 CAS 失败：
     - 说明至少有两个线程（`T` 和另一个线程）同时竞争该锁。
     - 检查对象头的 Mark Word 是否指向当前线程 `T` 自己的栈帧（**锁重入**）：
       - 如果是：在栈帧中再压入一个 Lock Record（内容为 null，仅用于计数重入次数），仍然持有锁。
       - 如果不是：说明发生了**竞争**，需要 **“自旋”** 等待一小段时间（自适应自旋，次数由 JVM 动态调整），再次尝试 CAS。
     - 如果自旋后 CAS 仍然失败（或者自旋达到阈值），则升级为**重量级锁**。
- **解锁过程：**
  1. 弹出栈顶的 Lock Record。
  2. 如果 Lock Record 中的 Displaced Mark Word 不为 null（表示是最后一次解锁或非重入解锁）：
     - 使用 **CAS** 尝试将 Displaced Mark Word **写回**对象头。
     - 如果 CAS 成功：解锁完成，锁状态回到无锁或可偏向。
     - 如果 CAS 失败：说明在持有锁期间，锁已经升级为**重量级锁**（有其他线程竞争导致自旋失败）。此时需要释放重量级锁并唤醒等待线程。
- **优点：** 在没有实际竞争（线程交替执行）时，避免了操作系统互斥量（mutex）的开销，使用 CAS 操作，速度快。
- **缺点：** 存在自旋消耗 CPU 的情况。如果竞争激烈（线程同时竞争），自旋会浪费 CPU，最终仍会升级。

##### 重量级锁

- **设计目标：** 处理**真正激烈竞争**的场景。当多个线程需要同时访问同步块时，未获得锁的线程会被操作系统挂起（阻塞），等待锁释放后被唤醒。
- **升级触发条件：**
  - 轻量级锁状态下，一个线程自旋获取锁失败（达到自旋阈值）。
  - 一个线程尝试获取轻量级锁时，CAS 失败且对象头 Mark Word 指向的不是自己的栈帧。
- **升级过程：**
  1. 撤销轻量级锁（或从无锁/偏向锁直接竞争激烈）。
  2. 在 JVM 层面创建一个与对象关联的 **Monitor 对象**（也称为管程或内置锁）。这个 Monitor 对象包含：
     - `_owner`：指向持有锁的线程。
     - `_EntryList`：等待锁的线程队列（竞争锁失败被阻塞的线程）。
     - `_WaitSet`：调用了 `wait()` 方法而等待的线程队列。
  3. 将对象头的 Mark Word 更新为指向这个 **Monitor 对象的指针**。
  4. 锁标志位设置为 `10`。
- **工作原理：**
  - 当线程 `T1` 成功获取重量级锁时，`_owner` 设置为 `T1`。
  - 线程 `T2` 尝试获取锁时，发现 `_owner` 不是自己，则 `T2` 会被放入 `_EntryList`，并调用操作系统内核的互斥量（mutex）将自己**挂起（阻塞）**。
  - 当 `T1` 释放锁时：
    - 将 `_owner` 设置为 null。
    - 从 `_EntryList` 中唤醒一个（或多个，取决于策略）等待线程去竞争锁（通常是非公平竞争）。
  - 被唤醒的线程需要重新尝试获取锁（可能再次失败，重新阻塞）。
- **优点：** 能处理高并发下的激烈竞争，阻塞线程不消耗 CPU。
- **缺点：** 涉及操作系统内核态与用户态切换、线程阻塞与唤醒，**性能开销最大**。

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
   1. 如果另一个任务试图在该表级别上应用共享或排它锁，则受到由第一个任务控制的表级别意向锁的阻塞。第二个任务在锁定该表前不必检查各个页或行锁，而只需检查表上的意向锁。(**意向锁不会与行级的共享 / 排他锁互斥！！！** **意向锁不会与行级的共享 / 排他锁互斥！！！** **意向锁不会与行级的共享 / 排他锁互斥！！！**)

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

### 性能优化

#### 慢查询优化

- **问题定位**：启用`slow_query_log`，使用`pt-query-digest`分析日志
- **计划解析**：`EXPLAIN`关键指标解读：`type`字段：避免ALL（全表扫描），追求range/index，`key_len`：索引使用长度，`Extra`：警惕Using temporary（临时表保存中间结果）/Useing filesort（进行了排序操作没走索引）
- 索引检查以及语句优化

#### 千万级别大表如何进行深度分页查询优化？

- 原因在于索引回表，即使用了二级索引没有使用一级索引的情况
- eg: select * FROM t_order ORDER BY create_time desc LIMIT 10000000,100; 优化为SELECT * FROM t_order WHERE create_time <= (SELECT create_time FROM t_order ORDER BY create_time desc LIMIT 1000000,1) ORDER BY create_time desc LIMIT 100;

#### 两个大表JOIN如何操作？



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

## RocketMQ

### 消息模型

![](img\RocketMQ消息模型.png)

- 为了消息写入能力的**水平扩展**，RocketMQ 对 Topic进行了分区，这种操作被称为**队列**（MessageQueue）。
- 为了消费能力的**水平扩展**，ConsumerGroup的概念应运而生。相同的ConsumerGroup下的消费者主要有两种负载均衡模式，即**广播模式**和**集群模式**（图中是最常用的集群模式）。
- **Name Server**：Topic 路由注册中心，支持 Topic、Broker 的动态注册与发现。生产者和消费者可以通过Name Server来找到Topic对应的Broker IP地址列表，Name Server集群节点之间是相互独立的，对于生产者、消费者、Broker来说是透明的。

### 工作流程

启动Name Server -> 启动Broker -> 创建Topic -> 生产者生成 -> 消费者消费

### 基础概念

#### TOPIC

**生产环境强烈建议管理所有主题的生命周期，关闭自动创建参数**，以避免生产集群出现大量无效主题，无法管理和回收，造成集群注册压力增大，影响生产集群的稳定性。

#### TAG

用来区分同一个 Topic 下相互关联的消息，例如全集和子集的关系、流程先后的关系。

#### KEYS

消息唯一标识

#### 队列

Topic分区

![](img\RocketMQ队列模型.png)

### 消息发送

#### 普通消息

**同步发送**：消息发送方发出一条消息后，会在收到服务端同步响应之后才发下一条消息的通讯方式

**异步发送**：消息发送方在发送了一条消息后，不需要等待服务端响应即可发送第二条消息，发送方通过回调接口接收服务端响应，并处理响应结果。异步发送一般用于链路耗时较长，对响应时间较为敏感的业务场景。

**单向传输**：发送方只负责发送消息，不等待服务端返回响应且没有回调函数触发，即只发送请求不等待应答。此方式发送消息的过程耗时非常短，一般在微秒级别。适用于某些耗时非常短，但对可靠性要求并不高的场景，例如日志收集。

#### 顺序消息

对于一个指定的Topic，消息严格按照先进先出（FIFO）的原则进行消息发布和消费，即先发布的消息先消费，后发布的消息后消费。

注意这里跟RabbitMQ不同的地方在于，RabbitMQ是天然的先进先出队列，RocketMQ是基于Topic来分片入队（即生产者顺序发送的消息可能分片到不同的队列导致消费顺序不一致），那么消息的顺序性无法天然保证（为了高并发高可用的牺牲）。

基础实现是要求在发送消息时传入MessageQueueSelector来让顺序消息进入同一个队列。

#### 延时消息

#### 批量消息

100个小消息不要投递一百次，放在集合里面投递一次即可（需要注意的是批量消息的大小不能超过 1MiB（否则需要自行分割），其次同一批 batch 中 topic 必须相同）。

#### 事务消息

![](img\RocketMQ事务消息.png)

基础实现是通过注册的TransactionListener来处理二阶段提交逻辑。

### 消息消费

以消费者组为基本单位

#### 集群模式

![](img\RocketMQ集群消费模式.png)

#### 广播模式

![](img\RocketMQ广播消费模式.png)

#### 消费位点

![](img\RocketMQ消费位点.png)

重复消费问题：一个消费者新上线后，同组的所有消费者要重新负载均衡重平衡 reBalance（反之一个消费者掉线后，也一样）。一个队列所对应的新的消费者要获取之前消费的 offset（偏移量，也就是消息消费的点位），此时之前的消费者可能已经消费了一条消息，但是并没有把 offset 提交给 broker，那么新的消费者可能会重新消费一次（消费者的消费offset并不是实时提交，而是通过将 offset 先保存在本地map中，通过定时任务持久化上去）。

#### 推、拉

- Push是服务端主动推送消息给客户端，优点是及时性较好，但如果客户端没有做好流控，一旦服务端推送大量消息到客户端时，就会导致客户端消息堆积甚至崩溃。
- Pull是客户端需要主动到服务端取数据，优点是客户端可以依据自己的消费能力进行消费，但拉取的频率也需要用户自己控制，拉取频繁容易造成服务端和客户端的压力，拉取间隔长又容易造成消费不及时。

## ElasticSearch

### 基础概念

#### 索引

文档的集合，类似于database。

索引中的字段无法修改。可以新增（新增字段后已存在的文档该字段内容是不显示的，需要手动赋值），无法删除字段（只能通过reindex方式-即重新创建一个索引，目前验证下来通过PUT Mapping方式并无效果）。

#### 分片

分片是Elasticsearch中存储数据的基本单位。一个索引可以由多个分片组成，每个分片都是一个完整的Lucene索引。通过分片，Elasticsearch可以将数据分布到多个节点上，从而实现数据的分布式存储和并行处理。官方推荐10-50GB/分片（实际结合业务数据量、硬件资源来妥协设置）。

**分片是es支持水平拓展的基石**。

实际存储大小不会完全均等，Elasticsearch会根据文档的_id哈希分配到不同分片，最终各分片数据量可能略有差异。

副本分片是其主分片的完整克隆，也是一个完整的Lucene索引。提供了容灾和**高并发查询**保障。

**高可用部署模式下主分片和副本分片在多服务节点上交叉分配（ES默认保证这一点）。**

### 语法

#### 索引

##### 创建

```java
PUT /my_index
{
  "settings": {
    // 索引基础设置
    "index": {
      "number_of_shards": 3,                // 主分片数（创建后不可修改）
      "number_of_replicas": 1,             // 每个主分片的副本数（可动态修改）
      "refresh_interval": "1s",             // 数据刷新间隔（默认1秒）
      "max_result_window": 10000,           // 最大返回结果数（默认10000）
      "auto_expand_replicas": "0-1"        // 自动扩展副本（0-1个，根据节点数）
    },
    
    // 分片分配策略
    "routing": {
      "allocation": {
        "include": {
          "_tier_preference": "data_content" // 优先在数据节点分配
        }
      }
    },
    
    // 分析器配置
    "analysis": {
      "analyzer": {
        "my_custom_analyzer": {             // 自定义分析器
          "type": "custom",
          "tokenizer": "standard",
          "filter": ["lowercase", "my_stopwords"]
        }
      },
      "filter": {
        "my_stopwords": {                  // 自定义停用词
          "type": "stop",
          "stopwords": ["a", "the", "is"]
        }
      }
    }
  },
  
  // 映射定义（文档结构）
  "mappings": {
    "dynamic": "strict",                   // 严格控制字段类型（禁止自动映射）
    "properties": {
      "title": {                           // 文本字段
        "type": "text",
        "analyzer": "my_custom_analyzer",  // 使用自定义分析器
        "fields": {
          "keyword": {                     // 子字段用于精确匹配/聚合
            "type": "keyword"
          }
        }
      },
      "price": {                           // 数值字段
        "type": "double",
        "index": true                      // 可被搜索（默认true）
      },
      "create_time": {                     // 日期字段
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss||epoch_millis"
      },
      "tags": {                            // 数组字段
        "type": "keyword"
      },
      "location": {                        // 地理坐标
        "type": "geo_point"
      },
      "product_info": {                     // 嵌套对象
        "type": "nested",
        "properties": {
          "spec": {"type": "keyword"},
          "weight": {"type": "float"}
        }
      }
    }
  },
  
  // 别名配置（可选）
  "aliases": {
    "search_alias": {},                     // 查询别名
    "write_alias": {                        // 写入别名
      "is_write_index": true
    }
  }
}
```

```yaml
字段类型:
	字符串:
		- text: 可分词检索，不支持聚合排序
		- keyword: 必须精确匹配
    数字:
    	- short
    	- byte
    	- int
    	- long
    	- float
    	- double
    日期:
    	- date
    布尔:
    	- boolean
    多字段:
    	- properties
    预定义字段:
    	- 以下划线开头的字段 (_id,_source等等)
```

##### 更新

```java
**索引字段赋默认值： 1. *indexTemplate的default属性* 2. *ingest Pipeline + _update_by_query* **

**索引增加新字段后如何为已有的文档数据赋默认值：** ***Ingest Pipeline + _update_by_query***

// 部分字段更新
POST /{index_name}/_update/{document_id}
{
  "doc": {
    "field1": "new_value",
    "field2": 100
  }
}
// 更新，不存在该ID文档则创建
POST /{index_name}/_update/{document_id}
{
  "doc": {
    "field": "new_value"
  },
  "doc_as_upsert": true  // 如果文档不存在，将 doc 内容作为新文档插入
}
// 批量更新 注意请求体必须用换行结尾
POST /_bulk
{ "update": { "_index": "products", "_id": "1" } }
{ "doc": { "price": 19.99 } }
{ "update": { "_index": "products", "_id": "2" } }
{ "script": { "source": "ctx._source.stock -= 1" } }

// 按条件更新
POST /{index_name}/_update_by_query
{
  "query": {
    "term": { "status": "old_status" }
  },
  "script": {
    "source": "ctx._source.status = 'new_status'"
  }
}

```

##### 查询

```java
//1. text字段内容查询 /index_name/_search
{
    "query": {
        "match": {
            "name": "余世"
        }
    },
    // 偏移量
    "from": 0,
    // 返回条数 默认10条
    "size": 100,
    "_source": ["name", "age"] // 指定返回的字段，默认返回全部_source
    "sort": [ // 指定排序字段，默认使用_score 
        {"name": "asc"}, // Text类型不支持排序噢会报错，请使用keyword类型
        {"age": "desc"}
    ]
}
//2. 范围查询
{
    "query": {
        "range": {
            "age": {
                "gte": 18, // >=18
                "lte": 20  // <=20
            }
        }
    }
}
// 3. 聚合查询 PS：聚合查询相当于在查询结果的基础上作运算，支持查询条件
{
    "query": {
        "range": {
            "age": {
                "gte": 18,
                "lte": 30
            }
        }
    },
    // 聚合键名称 固定值
    "aggs": {
        // 自定义聚合名称
        "max_age": {
            // 聚合类型 固定值 指标相关 max,min,avg,sum等 桶相关terms,filter等
            "max": {
                // 索引字段名
                "field": "age"
            }
        },
        "max_age_double": {
            "max": {
                "field": "age",
                "script": { // 最大值乘以2
                    "source": "_value * 2"
                }
            }
        }
    }
}
// 4. 全部查询
{
    "query": {
        "match_all": {}
    }
}
// 5. 批量查询（跨索引查询 提升性能）
POST /my_test1/_msearch // 指定了index 不同查询条件
{}
{"query":{"match_all":{}}}
{}
{"query":{"match":{"name":"张三"}}}

POST /_msearch // 不同index查询  不同查询条件
{"index":"my_test1"}
{"query":{"match_all":{}}}
{"index":"my_test2"}
{"query":{"match":{"name":"赵六"}}}

// 6. scroll api 高效检索大量数据（甚至全量数据），避免深度分页的性能问题。
// 初始化搜索 & 获取 Scroll ID
POST /your_index/_search?scroll=1m  // 保持上下文1分钟
{
  "size": 100,                      // 每批返回数量
  "query": { "match_all": {} }
}
// 响应
{
  "_scroll_id": "DXF1ZXJ5QW5kRmV0Y2gBAAAAAA...",
  "hits": { ... }                   // 首批100条数据
}
// 使用 Scroll ID 获取后续批次
POST /_search/scroll
{
  "scroll": "1m",                   // 重置超时时间
  "scroll_id": "DXF1ZXJ5QW5kRmV0Y2gBAAAAAA..."
}
// 清理上下文（重要！）
DELETE /_search/scroll
{
  "scroll_id": "DXF1ZXJ5QW5kRmV0Y2gBAAAAAA..."
}
```

##### 过滤器

|   特性   |                filter                |                  query                   |
| :------: | :----------------------------------: | :--------------------------------------: |
| 主要目的 |  精确匹配文档是否满足条件（是/否）   | 查找匹配文档并计算相关度评分（匹配程度） |
| 评分影响 |    不影响评分，结果仅包含匹配文档    |      计算并影响每个文档的相关度评分      |
| 缓存机制 |    结果自动缓存，后续查询直接复用    |      默认不缓存（某些特殊查询除外）      |
| 使用场景 | 结构化数据过滤（状态、范围、标签等） | 全文搜索、模糊匹配等需要相关性排序的场景 |
| 性能特点 |       高效，尤其对重复过滤条件       |             相对资源消耗更大             |

**过滤器是为了解决“高效、重复地缩小结果集范围”这个特定需求而存在的，它通过避免评分计算和利用结果缓存，在结构化数据过滤场景下提供了远超普通查询的性能优势。**

**`term` 查询**是用于在**未分词的精确值字段**（通常是 `keyword` 类型）中查找**完全匹配**指定值的核心查询。理解它的关键在于 **“不分词”** 和 **“精确匹配”** 这两个特性。天然适合放入filter上下文中。

```java
GET /products/_search
{
  "query": {
    "bool": {
      "must": [ // 相当于and
        { "match": { "description": "wireless headphones" } } // 全文搜索
      ],
      "filter": [ 
        { "term": { "category.keyword": "Electronics" } }, // 精确分类过滤
        { "term": { "in_stock": true } }                  // 布尔值精确匹配
      ]
    }
  }
}

GET /products/_search
{
  "query": {
    "bool": {
      "must": [
        { "term": { "category.keyword": "Laptop" }},
        { "range": { "ram_gb": { "gte": 16 }}}
      ],
      "must_not": [
        { "term": { "condition": "used" }},
        { "term": { "brand.keyword": "Dell" }}
      ],
      "should": [
        { "term": { "ports": "Thunderbolt" }},
        { "range": { "weight_kg": { "lt": 1.5 }}}
        "minimum_should_match": 1 // 显式声明（实际是默认值）
      ],
      "minimum_should_match": 0 // 显式声明（实际是默认值）
    }
  }
}

GET /products/_search
{
  "query": {
    "bool": {
      "filter": [
        {
          "exists": { // 字段必须存在于文档中且具有非空值
            "field": "inventory_count"  // 要求 inventory_count 字段必须存在
          }
        }
      ]
    }
  }
}

GET /products/_search
{
  "query": {
    "bool": {
      "must_not": [  // 逻辑非
        {
          "exists": {
            "field": "discontinued_date"  // 查找无停产日期的商品
          }
        }
      ]
    }
  }
}
```

bool 

| bool查询子句 | 等价二元操作 |                    含义                    |
| :----------: | :----------: | :----------------------------------------: |
|     must     |     AND      |                必须全部满足                |
|   must_not   |     NOT      |               必须全部不满足               |
|    should    |      OR      | 至少要满足minimum_should_match所设置的个数 |

##### 删除

```java
// 删除单个文档
DELETE /{index_name}/_doc/{id}
// 按条件删除
POST /<index_name>/_delete_by_query
{
  "query": {
    "match": {
      "field_name": "value"
    }
  }
}
// 批量删除文档
POST /<index_name>/_bulk
{"delete": {"_id": "id1"}}
{"delete": {"_id": "id2"}}
{"delete": {"_id": "id3"}}

// 删除索引
DELETE /<index_name>
    
// 批量删除索引
DELETE /index1,index2
DELETE /logs-*
    
// 关闭索引
如果关闭了一个索引，就无法通过 Elasticsearch 来读取和写入其中的数据，直到再次打开它
POST /<index_name>/_close
    
// 打开索引
POST /<index_name>/_open
```

### 文本分析

![](.\img\elasticsearch_analysis.png)

### 相关性评分

#### 打分机制

- 词频：一个词条在一个文档中出现的次数越多，就越相关
- 逆文档频率：一个词条在不同文档中出现的次数越多，就越不相关（物以稀为贵）

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

​	**流程图：**

![](img\ZK分布式锁实现流程.png)

Redis：

​	**借助其SETNX EX | PX 特性结合LUA脚本来原子性的加锁释放锁**

## SpringBoot

### 自动配置运行原理

#### 运行原理图

![](.\img\SpringBoot自动配置运行原理图.png)

#### 核心注解

@SpringBootApplication

![](.\img\@SpringBootApplication注解组成.png)



### AMQP

#### @RabbitListener

每一个方法都是独自的Container

#### container

##### SimpleMessageListenerContainer

SMLC为每个消费者使用一个内部队列和一个专用线程。如果一个容器被配置为监听多个队列，同一个消费者线程被用来处理所有的队列(也就是同一个Channel)

##### DirectMessageListenerContainer

### AOP

#### 表达式语法

execution(modifier? ret-type declaring-type?name-pattern(param-pattern) throws-pattern?)

- **modifier**：匹配修饰符，`public, private` 等，省略时匹配任意修饰符
- **ret-type**：匹配返回类型，使用 `*` 匹配任意类型
- **declaring-type**：匹配目标类，省略时匹配任意类型
- - `..` 匹配包及其子包的所有类
- **name-pattern**：匹配方法名称，使用 `*` 表示通配符
- - `*` 匹配任意方法
  - `set*` 匹配名称以 `set` 开头的方法
- **param-pattern**：匹配参数类型和数量
- - `()` 匹配没有参数的方法
  - `(..)` 匹配有任意数量参数的方法
  - `(*)` 匹配有一个任意类型参数的方法
  - `(*,String)` 匹配有两个参数的方法，并且第一个为任意类型，第二个为 `String` 类型
- **throws-pattern**：匹配抛出异常类型，省略时匹配任意类型

### 事务注解失效

1. 方法不是public（动态代理，多态必须得是Public）
2. @Transactional标注的方法被final修饰（被final修饰的方法无法被重写）
3. static修饰，静态方法（静态方法是类所属无法代理）
4. this.自调用（事务拦截是基于代理对象，不会直接修改类）
5. 异常未抛出/异常抛出类型与注解限制不匹配
6. 数据库存储引擎不支持事务
7. 多线程

## SpringCloud Alibaba

### Nacos

#### 灰度发布

发布配置时Beta发布即是灰度发布，填写需要生效的客户端IP，即可只对指定IP客户端生效。

#### 服务集群注册

如果你有三个服务的`application.name`配置相同，它们会被视为同一个服务的多个实例。Nacos控制台会将这些实例聚合在一起，展示为一个服务名，而不是三个独立的服务。

### Dubbo

#### gRPC

本质上是使用Http2+ProtoBuf实现

解决的是Http短链接频繁创建销毁的开销问题

Http2通过一个TCP链接的多路复用来提升效率，压缩传输消息头，二进制数据传输而非文本，支持数据推送。

### Seata

#### 基础角色

TC(Transaction Coordinator): 维护全局和分支事务状态，驱动全局事务的提交或回滚。

TM(Transaction Manager): 定义全局事务的范围：开始全局事务、提交或回滚全局事务。

RM(Resource Manager): 管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。

#### 领域模型 

![](img\seata领域模型.png)

#### 隔离级别

一般说事务隔离级别指的是本地事务隔离级别，由于Seata解决的全局事务一致性的问题，因此Seata的事务隔离级别指的是全局事务下若干个分支事务之间的隔离关系。

Seata（AT 模式）的默认全局隔离级别是 **读未提交（Read Uncommitted）**。

如果应用在特定场景下，必需要求全局的 **读已提交** ，目前 Seata 的方式是通过 SELECT FOR UPDATE 语句的代理。

#### TCC（Try-Confirm-Cancel）

### Gateway

满足断言-通过过滤器转发-到达目的地

## 功能设计

### 秒杀

特点：瞬时并发量非常高，读多写少，流程不复杂

方案：

- 数据预热（缓存）
- 异步解耦、削峰填谷（MQ）
- 限流降级，防止恶意请求（黑名单机制）
- LUA脚本原子化操作（防止超卖）

### 三高

![](.\img\三高.png)

### JWT

#### 干什么的

用户认证，支持跨域，服务端无状态。

### 设计模式

#### 装饰设计模式

![](img\装饰设计模式.png)

### 跨队列消息顺序性保证

#### 方案一

强制单线程消费，牺牲并发性，天然有序

#### 方案二

分区顺序（保证局部顺序不保证全局顺序，保证并发性）

- 给消息设置一个 **顺序 key（sharding key）**，例如用户ID、订单ID。
- 相同 key 的消息只会投递到同一个分区（即同一个队列/consumer线程）。
- 不同 key 可以并行，不同 key 的消息间不保证顺序。

#### 方案三

业务重排：业务层竞争同步资源+业务逻辑判断，缓存/延迟执行。

### MQ预取机制

本质上是一种消费者的本地消息缓冲，减少网络IO提高吞吐量。

预取的消息消费异常处理方案：***nack重新入队 / 放入死信队列进行延迟处理***

### 限流、降级、熔断

### 订单超时

![](.\img\Rabbitmq延迟队列.png)

### 订单超时支付成功

类比One2Data产品BaseQcow2超时创建成功

1. **明确订单(任务)终态**

   订单(任务)只有只有成功和失败两种最终状态，只要进入一种状态就不允许被修改。如果终态能够被随便转换那一定是设计不合理的。**订单状态流转表**要设计**清晰**，不允许逆向更新。

2. **业务处理**

   超时前支付(BaseQcow2已经成功创建)，超时后成功(Web已经判断通讯超时任务创建失败)。-- 兜底处理 收到支付通知时(CreateBaseQcow2Report)发现订单已经超时关闭进行退款(回调删除超时成功创建的Qcow2)

## 故障恢复

### MQ

## XXL-JOB

### 执行器负载均衡

可以通过调整任务的路由策略来实现

## Docker



## 面向对象编程思想

面向接口编程

## 心性

- 凡事缓则成，欲速则不达
- 社会属性：劳动产生价值，得到社会尊重认可
- 家庭属性：为家人带来幸福
- 群体属性：群体关系，个体差异
- 自我属性：自我认同

## 职业生涯

### 困境

- 成长到瓶颈期？
- 待遇水平跟不上市场？
- 种种不合理规章制度？
- 加班多？
- 同事相处不和谐？
- 氛围不好？
- 如何逃离舒适区？
- 离家太远，无法照顾父母？

### 如何看待离职

- 任何公司都存在不公平，困局，混乱
- 是否自己的工作已经处理到了一个比较理想的状态，想做的事，想做好的事，是不是做了，做好了。
- 无法再有任何的提升
- 绝不因人和事的因素离开
- 脱离困境的最好方式

### 个人

- 持续精进技术，硬技能提升

- 尽人事，只要还在工作，请认真负责的完成自己职责范围内的事务
- 广度向深度
- 情绪的稳定
- 软技能提升
- 接受挫折和失败
- 学习适应

### 最终目的

达到深层次的满足和幸福感

### 思维模式

#### 黄金圈法则

##### What（做什么）

每个组织和个人都知道自己“做什么”。

这是最外层，指的是具体的产品、服务、任务或行动。例如，一家公司生产手机、提供教育服务，或者一个人从事编程、设计等工作。

##### How（如何做）

一些组织或个人知道“如何做”。

这是中间层，指的是独特的过程、方法或策略，用以实现“做什么”。比如，某种技术流程、业务模式或工作方法，能够帮助他们与众不同。

##### Why（为什么）

很少有组织和个人真正明确“为什么”。

这是最内层、最核心的问题，涉及存在的目的、信念或使命。它回答“我们为什么存在？”、“我们为什么做这些事？”、“我们的核心价值是什么？”。
