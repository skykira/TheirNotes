<!-- TOC -->

- [1. Java 基础](#1-java-基础)
  - [1.1. HashMap](#11-hashmap)
  - [1.2. ConcurrentHashMap](#12-concurrenthashmap)
  - [1.3. Reference](#13-reference)
- [2. Java 并发](#2-java-并发)
  - [2.1. AQS](#21-aqs)
  - [2.2. 偏向锁](#22-偏向锁)
- [3. Java IO](#3-java-io)
- [4. Java 安全](#4-java-安全)
- [5. 函数式编程](#5-函数式编程)
- [6. JVM](#6-jvm)
  - [6.1. 垃圾收集](#61-垃圾收集)
  - [6.2. 字节码操作](#62-字节码操作)
  - [6.3. 调优](#63-调优)
- [7. 分布式](#7-分布式)
  - [7.1. Raft](#71-raft)
  - [7.2. BFT](#72-bft)
  - [7.3. 分布式锁](#73-分布式锁)
    - [7.3.1. Redis 分布式锁](#731-redis-分布式锁)
- [8. DateBase](#8-datebase)
- [9. Spring](#9-spring)
  - [9.1. 源码解析](#91-源码解析)
  - [9.2. 关键组件](#92-关键组件)
    - [9.2.1. `PostProcessor` bean 后置处理器](#921-postprocessor-bean-后置处理器)
- [10. Dubbo](#10-dubbo)
- [11. Tomcat](#11-tomcat)
- [12. Netty](#12-netty)
- [13. Kafka](#13-kafka)
- [14. Linux](#14-linux)
- [15. 编程基础](#15-编程基础)
- [16. 数据结构](#16-数据结构)

<!-- /TOC -->

# 1. Java 基础

- [`Class.this` 与 `this` 的区别](https://stackoverflow.com/questions/5666134/what-is-the-difference-between-class-this-and-this-in-java)

- [`toArray()` 与 `toArray(new String[0])`](https://stackoverflow.com/questions/18136437/whats-the-use-of-new-string0-in-toarraynew-string0)

- [`ThreadLocalMap` 概览](https://www.cnblogs.com/xzwblog/p/7227509.html)

    `ThreadLocal` 是 `Thread` 操纵自己的 `ThreadLocalMap` 的工具。

- [反射修改 `static final` 的成员变量](https://www.zhihu.com/question/47054187)

- [非静态内部类中的隐藏变量 `this$0` 指向外部类](https://stackoverflow.com/questions/28462849/what-does-it-mean-if-a-variable-has-the-name-this0-in-intellij-idea-while-deb/28462949)

- [反射方法 `getDeclaredField()` 与 `getField()` 的区别](https://docs.oracle.com/javase/tutorial/reflect/class/classMembers.html)

    父类属性与私有属性不可兼得。

- [反射加载自定义的 `java.lang.System` 类](https://zhuanlan.zhihu.com/p/77366251)

- [Java lambda 表达式内变量须为 final 原因](https://www.zhihu.com/question/28190927/answer/39786939)

- [理解 Java 的线程中断](https://blog.csdn.net/canot/article/details/51087772)

- [Java 自定义序列化](https://www.jianshu.com/p/352fa61e0512)

- [Java 动态代理](https://github.com/skykira/TheirNotes/tree/master/SourceCode/JDK%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86)

    - [JDK 自带动态代理源码分析](https://blog.csdn.net/weixin_43217817/article/details/102268504)

    > JDK 操作字节码生成所需要的代理类 `Class`，该代理类继承了 `Proxy` 并实现了我们的接口。Proxy 类实例提供了保存 `InvocationHandler` 自定义逻辑的功能。
    >
    > 代理类中所有的方法（包括类似 `toString()`）都通过我们自定义的 `InvocationHandler` 的 `invoke` 方法来实现，因此单个代理类的方法会被加入同样的逻辑。

    - [CGLib 动态代理](https://dannashen.github.io/2019/05/09/Cglib%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86/)

        拦截器中对被拦截方法的调用通过 `proxy.invokeSuper(obj, args);` 完成，相当于子类直接调用父类，比 `invokeHandler` 的反射调用快些，量大的话。

- [检查型异常与非检查型异常](https://blog.csdn.net/u013630349/article/details/50850880)

- [类加载器全盘负责思想的限制](https://blog.csdn.net/byamao1/article/details/62884720)

    > 调用者只会使用它自己的 ClassLoader 来装载别的类

## 1.1. HashMap

- [HashMap 源码分析](https://segmentfault.com/a/1190000012926722)

- [JDK1.7 HashMap infinite loop](https://my.oschina.net/u/1024107/blog/758588)

- [HashMap 删除节点时的树退化为链表](https://www.cnblogs.com/lifacheng/p/11032482.html)

## 1.2. ConcurrentHashMap

- [ConcurrentHashMap 源码分析](https://blog.csdn.net/u011392897/article/details/60479937)

- [ConcurrentHashMap 扩容图文详解](https://blog.csdn.net/ZOKEKAI/article/details/90051567)

    正在迁移的hash桶遇到 get 操作会发生什么？

    答：在扩容过程期间形成的 hn 和 ln 链是使用的类似于复制引用的方式，也就是说 ln 和 hn 链是复制出来的，而非原来的链表迁移过去的，所以原来 hash 桶上的链表并没有受到影响，因此从迁移开始到迁移结束这段时间都是可以正常访问原数组 hash 桶上面的链表，迁移结束后放置上fwd，往后的访问请求就直接转发到扩容后的数组去了。

- [ConcurrentHashMap 方法总结](https://juejin.im/post/5b001639f265da0b8f62d0f8#comment)

- [LongAdder 解析](https://juejin.im/post/6844903909127880717)

## 1.3. Reference

- [PhantomReference & Cleaner 的运行原理](https://zhuanlan.zhihu.com/p/29454205)

# 2. Java 并发

- [用户态内核态间切换耗时的原因](https://blog.csdn.net/u010727189/article/details/103401970)

- [Java 线程状态转换图](http://mcace.me/java%E5%B9%B6%E5%8F%91/2018/08/24/java-thread-states.html)

- [synchronized的实现原理](https://www.cnblogs.com/longshiyVip/p/5213771.html)

- [深入理解Java线程池：ThreadPoolExecutor](http://www.ideabuffer.cn/2017/04/04/%E6%B7%B1%E5%85%A5%E7%90%86%E8%A7%A3Java%E7%BA%BF%E7%A8%8B%E6%B1%A0%EF%BC%9AThreadPoolExecutor/#addWorker%E6%96%B9%E6%B3%95)

    > *池化思想*用于解决系统资源管理方面的问题，能够降低资源消耗，同时使资源可控。
    >
    > 在并发环境下，系统不能够确定在任意时刻中，有多少任务需要执行，有多少资源需要投入。导致以下问题：
    > 1. 频繁申请销毁调度，给系统带来额外消耗
    > 2. 对资源无限申请情况，缺少抑制手段
    > 3. 系统无法合理管理内部资源分布

    流程：
    未达到 corePoolSize，提交的任务被包装成 Worker，直接运行。否则，提交为任务，等待执行。若是任务队列满了，就添加新的 Worker，到达最大线程容量时，拒绝策略生效。

- [终止线程池原理](https://www.cnblogs.com/trust-freedom/p/6693601.html)

    调用 `shutdown()` 后，会将线程池状态置为 `SHUTDOWN` 并中断所有空闲线程，但正在执行任务的线程不会。
    
    `SHUTDOWN` 状态的线程将不再有新的任务加入，之前执行任务的线程执行完任务后，最终会阻塞在获取任务处，导致线程池无法从 `SHUTDOWN` 状态变为结束状态。

    因此，线程池的设计中，Doug Lea 在所有可能导致线程池产终止的地方安插了 `tryTerminated()`，尝试终止线程池，在其中判断如果线程池已经进入终止流程，没有任务等待执行了，但线程池还有线程，便中断唤醒一个空闲线程。当该线程被唤醒继续运行到退出时，会继续传播中断行为。

- [UncaughtExceptionHandler 解析](https://www.jianshu.com/p/f22efc8ef594)

## 2.1. AQS

- [CLH、MCS队列锁](https://www.cnblogs.com/sanzao/p/10567529.html)

    自旋锁具有一定的缺陷，非公平、线程饥饿、锁标识同步耗费资源，因此产生了队列锁，对多个自旋锁进行管理。

## 2.2. 偏向锁

- [偏向锁的批量重偏向与批量撤销](https://www.cnblogs.com/LemonFive/p/11248248.html)

    假设有类 A。

    1. 初始状态的对象 a 为可偏向未偏向状态

    2. 当`线程 1` 对对象 a1 加锁后，a1 对象头 markword 状态偏向于`线程 1`，之后`线程 1`加锁时，发现锁偏向于自己，无需 CAS 替换对象头，可直接进入同步块。
    3. `线程 2`想要对对象 a1 加锁，发现 a1 偏向于 `线程 1`，触发锁撤销，此时 a1 的锁升级为轻量级锁。
    4. `线程 2`在 `BiasedLockingDecayTime`(默认25s) 时间内对类 A 的偏向锁撤销数量达到重偏向阈值(BiasedLockingBulkRebiasThreshold)时，触发批量重偏向，epoch 值加一，相当于将 A 类所有对象的偏向锁失效，重置为可偏向未偏向状态。此时，若有线程对对象 a 加锁，对象将会偏向于该线程。
    5. `线程 x`(或其它任意线程) 对已偏向其它线程的对象 ax 加锁，触发偏向锁撤销，当锁撤销数量达到批量锁撤销阈值(BiasedLockingBulkRevokeThreshold)时，触发批量锁撤销，类 A 新生成对象 markword 初始状态为无锁不可偏向状态。

- `Eliminating Synchronization-Related Atomic Operations with Biased Locking and Bulk Rebiasing` [偏向锁论文阅读](https://www.jianshu.com/p/e47ad923dee5)

    `轻量级锁`的技术假设是，在实际程序中，大多数锁的获取是没有争用的。
    
    `偏向锁`的技术假设是，大多数监视器不仅是没有争用的，而且在它们的生命周期中仅有一个线程进入和退出。

    批量重偏向认为，当偏向锁撤销数量达到某个阈值时，对象当下的偏向已经没有收益了，需要重新选择偏向。批量锁撤销认为，达到该阈值时，该类确实存在竞争，已经不适合使用偏向锁了。

    偏向锁的升级过程：

    1. T1 持有 a1 的偏向锁，T2 获取锁时，发现 a1 偏向 T1。
    2. 查看 a1 markword 中 epoch 值是否与类 A 的 epoch 值一致，不一致则表示 a1 当前的偏向无效，可以直接将 a1 偏向于自己。
    3. 如果一致，开始执行锁撤销。
    4. 当程序运行到全局安全点时，T2 遍历偏向锁持有线程的线程栈，查找与对象 a1 相关的 `Lock Record`。
    
    > 偏向锁获取时，会在当前线程线程栈中置入一个空置的 `Lock Record`，且无需 CAS 填入 `displaced markword`。

    5. 遍历到后，将轻量级锁需要的 markword 填充进去，完成轻量级锁升级。
    6. 如果没有遍历到，则将对象 markword 状态改为可偏向未偏向状态，然后重新 CAS 尝试获取锁。

- [对象计算 hashcode 将导致偏向锁膨胀](https://www.zhihu.com/question/52116998)

    当一个对象当前正处于偏向锁状态，并且需要计算其identity hash code的话，则它的偏向锁会被撤销，并且锁会膨胀为重量锁。

- [Java 中 `volatile` 的语义](https://www.jianshu.com/p/4e59476963b0)

- [Java中的偏向锁、轻量级锁、重量级锁解析](https://blog.csdn.net/lengxiao1993/article/details/81568130?depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1&utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1)

- [Java中的锁降级](https://www.zhihu.com/question/63859501)
  - [Java中的锁级降提案✨✨](http://openjdk.java.net/jeps/8183909)

    该优化提案显示，目前，实现重量级锁的 `monitor` 对象可以在STW时被清除，清除的是那些只有 `VM Thread`会去访问它们的 `monitor` 对象（也就是，不再使用的 `monitor` 对象）。

    因此，重量级锁可以降级为轻量级锁。

- [`ReentrantReadWriteLock` 保证 `readHolds` 的可见性](https://stackoverflow.com/questions/1675268/java-instance-variable-visibility-threadlocal)

- [LockSupport.park() 原理](https://blog.csdn.net/anlian523/article/details/106752414)

    如果中断状态为true，那么park无法阻塞。

# 3. Java IO

- [Java I/O体系从原理到应用](https://mp.weixin.qq.com/s/khyOVIqFp1vNK29OIMBBuQ)

- [阻塞非阻塞与同步异步的理解](https://www.zhihu.com/question/19732473/answer/88599695)

  [概念理解](https://www.zhihu.com/question/19732473/answer/871835155): 
  同步异步关注的是，通信双方的协作模式，同步则意味着针对当前业务，双方需要串行进行，异步则可以并行进行。阻塞非阻塞关注的是，调用者的状态，在业务等待期，是阻塞等待还是忙等。

  在不同层次，上述概念有不同的表现形式。
   - 在进程通信层面，同步则意味着阻塞，异步则意味着非阻塞。
   - 在Linux I/O层面，同步可以阻塞，可以非阻塞，异步为非阻塞。
   - 在[网络通信](https://www.zhihu.com/question/19732473/answer/308092103)中，主要是同步阻塞和异步非阻塞。同步意味着，发送方发出请求后等待返回结果，然后发出下一次请求。异步意味着，发送方不必等待结果可发送下一次请求。

  总的来说，[异步是编程语言和调用的API协同模拟出来的一种程序控制流风格](https://www.zhihu.com/question/19732473/answer/103182244)。程序可以在同步 API 上模拟出异步调用(比如多线程)，也可以屏蔽底层的异步接口。

# 4. Java 安全

- [如何理解恶意代码执行 `AccessController.doPrivileged()`](https://stackoverflow.com/questions/37962070/malicious-code-running-accesscontroller-doprivileged)

- [java沙箱绕过](https://www.anquanke.com/post/id/151398)

# 5. 函数式编程

- [`BiConsumer` 为什么可以引用仅有一个参数的方法](https://stackoverflow.com/questions/58046693/biconsumer-and-method-reference-of-one-parameter)

# 6. JVM

- [图说 Java 字节码指令](https://segmentfault.com/a/1190000008606277)

- [Java 编译器优化和运行期优化概览](https://blog.csdn.net/u013305783/article/details/81279175)

- [`Thread-Local Handshakes` 优化安全点STW](https://openjdk.java.net/jeps/312)

    对象的偏向锁撤销操作也可以无需程序运行到 `SafePoint`

- [Java启动参数 `javaagent` 的使用](https://www.cnblogs.com/rickiyang/p/11368932.html)

- [JVM 创建对象时的快速分配与慢速分配](https://umumble.com/blogs/java/how-does-jvm-allocate-objects%3F/)

    JVM 创建对象的过程：
    
    1. 尝试在TLAB中分配对象
   
    2. 如果TLAB中没有空间，则可以使用原子指令从eden分配新的TLAB或直接在eden中创建对象
   
    3. 如果伊甸园中没有地方，那么将进行垃圾收集
   
    4. 如果之后没有足够的空间，则尝试在旧一代中进行分配
   
    5. 如果它不起作用，那么报 OOM
   
    6. 对象设置标志头，然后调用构造函数

- [缓存行伪共享 (False Sharing)](http://ifeve.com/falsesharing/)

- [理解 TLAB](https://www.jianshu.com/p/2343f2c0ecc4)

## 6.1. 垃圾收集

- [CMS 垃圾收集过程](https://zhuanlan.zhihu.com/p/54286173)

  - [concurrent-preclean 和 concurrent-abortable-preclean 两个阶段的作用](https://zhuanlan.zhihu.com/p/150696908)

    YGC 时，卡表的作用是找到跨带引用。此时，卡表的作用是记录并发标记阶段被应用程序并发修改的对象引用。

    preclean 阶段是对这些 card marking 产生的 dirty card 进行 clean，CMS GC 线程会扫描 dirty card 对应的内存区域，更新之前记录的过时的引用信息，并且去掉 dirty card 的标记。

    concurrent-abortable-preclean 用于调度 remark 的开始时机，防止连续 STW。


- [G1 垃圾回收算法原理](https://hllvm-group.iteye.com/group/topic/44381)

    - [G1 垃圾回收算法总览](https://www.jianshu.com/p/a3e6a9de7a5d)

    - [深入理解 G1 的 GC 日志](https://club.perfma.com/article/233563)

    - [G1 SATB和 Incremental Update 算法的区别](https://www.jianshu.com/p/8d37a07277e0)

        SATB 认为标记开始时，所有活着的对象在之后并发标记时也是存活的，因此当白对象断开某个引用时，将该引用压入遍历堆栈，也就是将该白对象变为灰对象。

        Incremental Update 在某个黑对象又引用了某个白对象时，会将该黑对象置灰，因此该算法在完成并发 marking 后 需要重新扫描根集合，重新将灰置黑。

    - [Region 两个 Bitmap 详解 ](https://mp.weixin.qq.com/s/5BIFme6bmyOA0WbKOllbjw)

        Region 的 prevTAMS 和 nextTAMS 用于记录并发标记过程中，新分配的对象，同时存储个快照。

    - [G1-Card Table 和 RSet 的关系](https://blog.csdn.net/luzhensmart/article/details/106052574)

    - [RSet 结构解释](https://zhuanlan.zhihu.com/p/130479811?utm_source=wechat_session&utm_medium=social&utm_oi=70601460940800)

        RSet 是一个 points-into 结构，记录了谁引用了我。
        
        它可以看做一个 Hashtable<key,int[]>。key 为其它 Region，int[] 代表了其它 Region 的 `Card Table`。垃圾收集时，找到 RSet 中指示的区域，作为 GCRoots 的一部分。

- [ZGC 的特点](https://mp.weixin.qq.com/s/KUCs_BJUNfMMCO1T3_WAjw)

- [Full GC 时新生代的垃圾收集方式](https://www.zhihu.com/question/62604570/answer/201129934)

    CMS 之前的垃圾收集器并不能单独收集老年代，Full GC 对整个堆使用 `MSC(Mark-Sweep-Compact)` 算法。

- [Major GC 和 Full GC 的区别](https://www.zhihu.com/question/41922036/answer/93079526)

- [ParallelScavenge 的 fullGC](https://www.zhihu.com/question/48780091/answer/113063216)

- [GCLocker-initiated young GC 多余发生](https://www.jianshu.com/p/ecc57a81f73c)

## 6.2. 字节码操作

- [理解字节码增强工具包 Instrumentation ](https://www.throwable.club/2019/06/29/java-understand-instrument-first/)

    包括 `premain` 和 `agentmain` 的使用

- [Java 字节码增强技术](https://tech.meituan.com/2019/09/05/java-bytecode-enhancement.html)


## 6.3. 调优

- [jmap 指令慎用](https://blog.csdn.net/seeJavaDocs/article/details/53643227)

# 7. 分布式

## 7.1. Raft

- [客户端只读请求的处理](https://zhuanlan.zhihu.com/p/36592467)

## 7.2. BFT

- [PBFT 算法各阶段消息发送数量证明](https://zhuanlan.zhihu.com/p/53897982)

## 7.3. 分布式锁

### 7.3.1. Redis 分布式锁

- [基于Redis的分布式锁到底安全吗?](https://www.jianshu.com/p/dd66bdd18a56)

- [Redission 实现](https://www.jianshu.com/p/f302aa345ca8)

    为了防止用户加锁后，解锁失败，导致其余客户端无法继续获取锁，所以需要给分布式锁一个过期时限，保证解锁失败后，锁能过期自动释放。
    
    这是一种典型的[租约机制](https://zhuanlan.zhihu.com/p/101913195)。

    > 锁的失效时间设置是个纠结的问题。当用户并不知道自己将会用多久的锁时，我们为该锁设置一个较小的租期，同时每隔一段时间，在该锁过期之前，自动续租。用户获得锁后，可以启动一个后台线程，周期性查询用户是否还持有锁，持有则续期。

# 8. DateBase

- [MySQL LEFT JOIN/ INNER JOIN/RIGHT JOIN 执行过程](https://learnku.com/articles/27701)

- [MySQL 多表 Join (表连接) 和 Where 间的执行顺序：`nested loop join` 机制](https://blog.csdn.net/qq_27529917/article/details/78447882)

    每 Join 一次后执行 where 过滤结果集，交叉执行。

- [利用 `sum()` 统计列中某个值的数目](https://blog.csdn.net/lavorange/article/details/25004181)

- [MySQL 组内排序](https://www.jianshu.com/p/717c4bdad462)

- [MVCC 是否解决了幻读](https://segmentfault.com/a/1190000020680168)

- [数据库事务中的一致性](https://www.zhihu.com/question/31346392)

- [Innodb 双写防止 `partial page write`](https://blog.csdn.net/jolly10/article/details/79791574)

- [ICP 索引下推](https://database.51cto.com/art/201907/599968.htm)

    针对辅助索引能覆盖到的列，将 where 条件的判断下推到存储引擎层。

# 9. Spring

- [GenericTypeResolver.resolveTypeArguments(Class<?> clazz, Class<?> genericIfc)](https://stackoverflow.com/questions/34271764/generictyperesolver-resolvetypearguments-returns-null)获取继承泛型类的子类的泛型类型

    对于 `AsyncConfigurationSelector` 类
    ```java
    public class AsyncConfigurationSelector extends AdviceModeImportSelector<EnableAsync>{
        ...
        public void someMethod(){
            Class<?> annType = GenericTypeResolver.resolveTypeArgument(this.getClass(), AdviceModeImportSelector.class);
            //annType 类型便是继承的父类的泛型类型
        }
    }
    ```

- [`$$` 与 `<generated>` 代表的含义](https://stackoverflow.com/questions/33605246/what-does-and-generated-means-in-java-stacktrace)

## 9.1. 源码解析

- [`@Configuration` 源码解析](https://mp.weixin.qq.com/s/8SpwGLMn_ewmT7h6Cn88_Q)

    由 `ConfigurationClassPostProcessor` 完成对配置类的代理操作

    - `postProcessBeanFactory` 方法

        1. 列出 `beanFactory` 中被 `@Configuration` 注释的 `beanDefinition`
        2. 将上面每一个 `beanDefinition` 的 `beanClass` 替换为基于 CGlib 的代理类
        3. 代理类自带两个拦截器
           -  `new BeanMethodInterceptor()`
                
                代理 `beanMethod` 方法，控制 bean 的创建或获取。只有第一次调用调用原方法，后续从 `beanFactory` 中获取 `bean`

           -  `new BeanFactoryAwareMethodInterceptor()`

                为代理类注入 `beanFactory` 属性

- @Async 源码解析

    1. `@EnableAsync` 引入 `AsyncConfigurationSelector.class`，然后引入 `ProxyAsyncConfiguration.class`，最终引入一个 bean `AsyncAnnotationBeanPostProcessor`。
    2. `AsyncAnnotationBeanPostProcessor`会生成并持有一个切面 `AsyncAnnotationAdvisor`。
    3. 当扩展点 `postProcessAfterInitialization()` 被调用时，判断
       1. 当前 bean 已经是代理对象时，判断切面能否应用 bean 的方法，可以则放入代理的切面中。
       2. 当前 bean 非代理对象时，切面合格，则创建代理，否则返回原始 bean。

- [Bean 创建过程源码解析](https://segmentfault.com/a/1190000022309143#item-3-3)

  - createBean 中的扩展点

    1. 执行 bean 实例化前置处理器, `InstantiationAwareBeanPostProcessor` 的 `postProcessBeforeInstantiation()` 方法
   
    2. bean 实例化
    3. `MergedBeanDefinitionPostProcessor` 的 `postProcessMergedBeanDefinition()` 方法
    4. 执行 bean 实例化后置处理器, `InstantiationAwareBeanPostProcessor` 的 `postProcessAfterInstantiation()` 方法
    5. 属性值注入前，进行处理`InstantiationAwareBeanPostProcessor` 的 `postProcessPropertyValues()` 方法
    6. 应用属性值，解析属性值中的 bean 引用, 未加载的去加载
    7. 调用 bean 前置处理器, `BeanPostProcessor` 的 `postProcessBeforeInitialization()`
        - 此时会执行 `@PostConstruct`
    8. bean 初始化，若是 InitializingBean, 调用 `afterPropertiesSet()`
    9.  调用自定义 `init-method`
    10. 调用 bean 后置处理器, `BeanPostProcessor` 的 `postProcessAfterInitialization()`
        - AOP 可在此时返回新的 bean 实例
    11. 调用 `DestructionAwareBeanPostProcessor` 的 `requiresDestruction` 方法, 判断时候需要注册 bean 销毁逻辑

- [`AbstractApplicationContext` 的 `refresh()` 方法源码解析](https://segmentfault.com/a/1190000022425759)

- [Spring代理创建及 AOP 链式调用过程](https://blog.csdn.net/l6108003/article/details/106577515)

    创建 bean 的过程中，在扩展点 `AbstractAutoProxyCreator.getEarlyBeanReference(）` 或 `AbstractAutoProxyCreator.postProcessAfterInitialization()` 处，获取 `AbstractAutoProxyCreator.wrapIfNecessary()` 方法检测类型为 `Advisor.class` 的 bean 以及被 `@Aspect` 注解注释(主要)的 bean。
    
    将切面 bean 中的增强方法封装为 Advisor，并将切面方法按照固定顺序排序。

    切面 bean 按照 `PriorityOrdered`、`Ordered` 接口或 `@Order` 注解顺序排序。

    选出合格的切面后，创建当前 bean 的代理。

    以 JdkDynamicAopProxy 为例，它作为 InvocationHandler，在调用到它的 invoke() 方法时，它会将上面合格的切面生成为 MethodInterceptor 的调用链，每当代理类方法被调用时，切面方法将被应用。

  - [AOP 切面执行顺序](https://blog.csdn.net/qq_32331073/article/details/80596084?utm_medium=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase)

    对于切面内部的 Advisor 顺序，为了满足 `Around -> Before -> Around -> After -> AfterReturning -> AfterThrowing` 的顺序，在执行时的 Advisor 调用链中，它们的顺序如下图所示，该顺序在为 bean 生成代理时便排好序了。

    ![同一个Aspect内Advisor的顺序
](https://github.com/skykira/TheirNotes/blob/master/source/picture/%E5%90%8C%E4%B8%80%E4%B8%AAAspect%E5%86%85Advisor%E7%9A%84%E9%A1%BA%E5%BA%8F.png?raw=true)

- [Spring 循环依赖](https://blog.csdn.net/f641385712/article/details/92801300)

    循环依赖是因为对象之间循环引用，造成闭环。造成循环依赖情况包括，构造器注入、prototype 模式的属性注入、单例模式的属性注入或方法注入三种。
    
    Spring 能解决单例模式的属性注入造成循环依赖的情况。解决方法是将 bean 实例化后初始化前的中间态暴露出来，然后通过三级缓存保证引用正确。

  - [Spring 解决循环依赖为什么使用三级缓存，而不是二级缓存](https://www.cnblogs.com/grey-wolf/p/13034371.html)

    `getEarlyBeanReference()` 为 `SmartInstantiationAwareBeanPostProcessor` 提供了在 bean 初始化前暴露自己的机会，用于解决初始化 bean 时， `BeanPostProcessor` 最终将 bean 包装为代理类，而之前暴露出去的 bean 是原始类的问题。

  - [@Async 导致循环依赖报错原理](https://blog.csdn.net/f641385712/article/details/92797058)

    以[该循环依赖](https://github.com/skykira/TheirNotes/tree/master/source/SourceCode/%40Async)为例阐述报错流程: 
    
    1. getBean -> doGetBean -> createBean -> doCreateBean 获取 C, 创建原始bean `c`，将 c 加入三级缓存 `singletonFactories` 中

    2. populateBean 填充 C 属性 D
    3. getBean -> doGetBean -> createBean -> doCreateBean 获取 D，创建原始bean `d`，将 d 加入三级缓存 `singletonFactories` 中
    4. populateBean 填充 D 属性 C
    5. doGetBean 时，从三级缓存中获取 c 的早期引用。假设有切面，此时经过后置处理器生成 `c` 的代理类对象（一号），@Transactional 注解生成代理也是通过插入切面来完成的，不会额外创建代理。代理类 `c`（一号） 赋给了 `d`
    6. initializeBean 初始化 `d`，有切面，生成代理类 `d`。然后经过 @Async 的后置处理器时，@Async 的后置处理器做了判断，如果传入的是代理类，则直接将增强添加到当前代理中，不会重新创建新的代理类。最终 d 初始化完成，成为代理类 `d`。`d` 没有暴露早期引用，无需进行循环依赖检查。
    7. 代理类 `d` 赋值给了 `c`
    8. initializeBean 初始化 `c`，因为暴露早期引用时已经进行过切面代理，不再进行代理，然后就返回了原始 `c`！然后经过 @Async 的后置处理器时，因为 @Async 并没有实现早期引用逻辑，此时需要对原始 `c` 进行代理，生成代理类 `c`(二号)。
    9.  初始化完成后，因为 `c` 的早期引用暴露出去了，因此需要循环依赖检查。发现 `d` 依赖 `c`，且位于 `alreadyCreated` 中，说明创建过，得到过 `c` 的早期引用(一号)，因此单例 `c` 出现了不同的版本在不同的引用中，报错。
    

## 9.2. 关键组件

### 9.2.1. `PostProcessor` bean 后置处理器

- [`AbstractAdvisorAutoProxyCreator` 决定是否要对当前 bean 进行代理](https://blog.csdn.net/z69183787/article/details/83311522)

    > spring 依赖注入时，什么时候会创建代理类，什么时候是普通 bean？

# 10. Dubbo

- [接口自适应类 T$Adaptive 查看](https://blog.csdn.net/swordyijianpku/article/details/105737163?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2)

# 11. Tomcat

- [Tomcat 架构解析](https://mp.weixin.qq.com/s/fU5Jj9tQvNTjRiT9grm6RA)

- [Tomcat 处理请求过程源码解析](https://blog.csdn.net/leileibest_437147623/article/details/85287568?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase)

# 12. Netty

> Reactor 模式
> 
> Reactor模式是一个事件驱动，用于一种处理一个或多个客户端并发进行服务请求的设计模式。
> 
> 它将服务端接收请求与事件处理分离，提高了系统处理并发的能力，`java NIO 的 reactor 模式是基于系统内核的多路复用技术实现的`。

- [SocketChannel 与 ServerSocketChannel 区别](https://blog.csdn.net/hzmlg1988/article/details/88082492)

- [Netty 启动源码分析](https://mp.weixin.qq.com/mp/appmsgalbum?action=getalbum&album_id=1342147420482011137&__biz=MzI2NzY4MjM1OQ==#wechat_redirect)(猿灯塔|需微信中打开)

- [netty 时间轮设计](https://zacard.net/2016/12/02/netty-hashedwheeltimer/)

# 13. Kafka

- [Kafka 的 push 与 pull 设计](https://blog.csdn.net/my_momo_csdn/article/details/93921625?utm_medium=distribute.pc_relevant.none-task-blog-baidulandingword-1&spm=1001.2101.3001.4242)

- [kafka 时间轮设计](https://blog.lovezhy.cc/2020/01/11/Kafka%E6%8C%87%E5%8D%97-%E6%97%B6%E9%97%B4%E8%BD%AE%E5%AE%9E%E7%8E%B0/)

    currentTime 在有了 queue 之后，就没有其他作用了，主要就是在 add 方法中拦住即将过期或者已经过期的任务。

    上级时间轮降级时，对于 timerTaskEntry 需要重新插入。

# 14. Linux

- Linux IO 模型
  - [简述 Linux IO 模型](https://mp.weixin.qq.com/s/3C7Iv1jof8jitOPL_4c_bQ)
  - [详述 Linux IO 模型](https://www.jianshu.com/p/486b0965c296)

- [多路复用之select、poll、epoll](https://www.wemeng.top/2019/08/22/%E8%81%8A%E8%81%8AIO%E5%A4%9A%E8%B7%AF%E5%A4%8D%E7%94%A8%E4%B9%8Bselect%E3%80%81poll%E3%80%81epoll%E8%AF%A6%E8%A7%A3/)

- [Linux 零拷贝技术](https://mp.weixin.qq.com/s/0SHaQBgMJ4MlKjX6m08EpQ)

  Linux I/O 设备与主存信息传送的控制方式分为程序轮询、中断、DMA等。

  在 Linux 中零拷贝技术主要有 3 个实现思路:

    1. 用户态直接 I/O
    2. 减少数据拷贝次数
    3. 写时复制技术

  以读取磁盘数据，写入网卡为例，实现方式：

    1. 用户态直接 I/O
      
        内核仅进行必要的配置，虽然还有用户内核空间上下文切换的开销，但减少了 CPU 拷贝

    2. mmap() + write
      
        将用户缓冲区的部分区域映射到内核缓冲区，减少了一次内核向用户缓冲的 CPU 拷贝

    3. sendfile

        sendfile 命令之前，读取写入通过 read() 和 write() 命令，存在四次上下文切换以及向用户空间的冗余拷贝。

        Sendfile 调用中 I/O 数据对用户空间是完全不可见的，完全交由内核去进行数据传输。不过，问题是，过程中用户无法对数据进行修改了。

    4. Sendfile + DMA gather copy

        磁盘读入到内核缓冲区的数据，本来需要传输到 Socket 缓冲区，然后才通过 DMA 拷贝到网卡中。

        如今，在硬件的支持下，Sendfile 拷贝方式仅仅是将数据的描述信息，即，缓冲区文件描述符和数据长度的拷贝，拷贝到 Socket 缓冲区中。

        发往网卡时，直接从内核缓冲区中取。又减少了一次 CPU 拷贝。

    5. Splice

        Sendfile 只适用于将数据从文件拷贝到 socket 套接字上，而 Splice 系统调用，不仅不需要硬件支持，还实现了两个文件描述符之间的数据零拷贝。

    6. 写时复制

        内核缓冲区被多个进程共享时，降低了系统开销。

    7. 缓冲区共享

        通过缓冲区池，它能被同时映射到用户空间（user space）和内核态（kernel space），完全省去了拷贝。


# 15. 编程基础

- [正则表达式的环视](https://blog.csdn.net/lxcnn/article/details/4304754)

    > `str.replaceFirst("(?<=.{5}).+", "...")`
    >
    > 保留 `str` 的前五位字符，其余字符用 `...` 代替
    
    - [正则表达式参考文档](http://notes.tanchuanqi.com/tools/regex.html)

- [TCP/IP 三次握手思考](https://blog.csdn.net/lengxiao1993/article/details/82771768?utm_medium=distribute.wap_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.wap_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase)

- [TCP/IP 四次挥手](https://blog.csdn.net/ThinkWon/article/details/104903925?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-3.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-3.nonecase)

- 将 javassist 动态生成的类打印出来

    `(ClassGenerator)ccp.getClassPool().get("com.alibaba.dubbo.common.bytecode.Proxy0").debugWriteFile()`

# 16. 数据结构

- [Append-only B+ Tree](https://blog.csdn.net/lpstudy/article/details/83722007)

- [LevelDB 设计与实现 —— LSM tree](https://blog.csdn.net/anderscloud/article/details/7182165)

- [红黑树工具](https://rbtree.phpisfuture.com/)

- [红黑树与 AVL 树比较](https://www.zhihu.com/question/19856999/answer/1254240739)