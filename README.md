<!-- TOC -->

- [Java 基础](#java-基础)
- [Java 并发](#java-并发)
- [Java 安全](#java-安全)
- [函数式编程](#函数式编程)
- [JVM](#jvm)
  - [调优](#调优)
- [分布式](#分布式)
  - [Raft](#raft)
  - [BFT](#bft)
- [DateBase](#datebase)
- [Spring](#spring)
- [编程基础](#编程基础)

<!-- /TOC -->

# Java 基础

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

- [JDK 动态代理]()

# Java 并发

- [Java 线程状态转换图](http://mcace.me/java%E5%B9%B6%E5%8F%91/2018/08/24/java-thread-states.html)

- [对象计算 hashcode 将导致偏向锁膨胀](https://blog.csdn.net/P19777/article/details/103125545)

- [偏向锁的批量重偏向与批量撤销](https://www.cnblogs.com/LemonFive/p/11248248.html)

    未达到批量重偏向阈值时，偏向锁膨胀为轻量级锁；达到批量重偏向阈值时，锁对象才会进行重偏向操作，且重偏向只能有一次。

- [Java 中 `volatile` 的语义](https://www.jianshu.com/p/4e59476963b0)

- [Java中的偏向锁、轻量级锁、重量级锁解析](https://blog.csdn.net/lengxiao1993/article/details/81568130?depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1&utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1)

- [Java中的锁降级](https://www.zhihu.com/question/63859501)
  - [Java中的锁级降提案✨✨](http://openjdk.java.net/jeps/8183909)

    该优化提案显示，目前，实现重量级锁的 `monitor` 对象可以在STW时被清除，清除的是那些只有 `VM Thread`会去访问它们的 `monitor` 对象（也就是，不再使用的 `monitor` 对象）。

    因此，重量级锁可以降级为轻量级锁。

- [`ReentrantReadWriteLock` 保证 `readHolds` 的可见性](https://stackoverflow.com/questions/1675268/java-instance-variable-visibility-threadlocal)

# Java 安全

- [如何理解恶意代码执行 `AccessController.doPrivileged()`](https://stackoverflow.com/questions/37962070/malicious-code-running-accesscontroller-doprivileged)

- [java沙箱绕过](https://www.anquanke.com/post/id/151398)

# 函数式编程

- [`BiConsumer` 为什么可以引用仅有一个参数的方法](https://stackoverflow.com/questions/58046693/biconsumer-and-method-reference-of-one-parameter)

# JVM

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

- [CMS 垃圾收集过程](https://zhuanlan.zhihu.com/p/54286173)

- [G1 垃圾回收算法原理](https://hllvm-group.iteye.com/group/topic/44381)

    - [G1 垃圾回收算法总览](https://www.jianshu.com/p/a3e6a9de7a5d)

    - [G1 SATB和 Incremental Update 算法的区别](https://www.jianshu.com/p/8d37a07277e0)

        SATB 认为标记开始时，所有活着的对象在之后并发标记时也是存活的，因此当白对象断开某个引用时，将该引用压入遍历堆栈，也就是将该白对象变为灰对象。

        Incremental Update 在某个黑对象又引用了某个白对象时，会将该黑对象置灰，因此该算法在完成并发 marking 后 需要重新扫描根集合，重新将灰置黑。

    - [PrevBitmap 和 NextBitmap 的作用](https://www.jianshu.com/p/aef0f4765098)

        Region 的 prevTAMS 和 nextTAMS 用于记录并发标记过程中，新分配的对象，同时存储个快照。write barrier 用于记录已标记的对象引用发生更改的情况。

    - [G1-Card Table 和 RSet 的关系](https://blog.csdn.net/luzhensmart/article/details/106052574)

- [ZGC 的特点](https://mp.weixin.qq.com/s/KUCs_BJUNfMMCO1T3_WAjw)

- [理解字节码增强工具包 Instrumentation ](https://www.throwable.club/2019/06/29/java-understand-instrument-first/)

    包括 `premain` 和 `agentmain` 的使用

- [理解 TLAB](https://www.jianshu.com/p/2343f2c0ecc4)


## 调优

- [jmap 指令慎用](https://blog.csdn.net/seeJavaDocs/article/details/53643227)

# 分布式

## Raft

- [客户端只读请求的处理](https://zhuanlan.zhihu.com/p/36592467)

## BFT

- [PBFT 算法各阶段消息发送数量证明](https://zhuanlan.zhihu.com/p/53897982)

# DateBase

- [MySQL LEFT JOIN/ INNER JOIN/RIGHT JOIN 执行过程](https://learnku.com/articles/27701)

- [MySQL 多表 Join (表连接) 和 Where 间的执行顺序：`nested loop join` 机制](https://blog.csdn.net/qq_27529917/article/details/78447882)

    每 Join 一次后执行 where 过滤结果集，交叉执行。

- [利用 `sum()` 统计列中某个值的数目](https://blog.csdn.net/lavorange/article/details/25004181)

- [MySQL 组内排序](https://www.jianshu.com/p/717c4bdad462)

- [MVCC 是否解决了幻读](https://segmentfault.com/a/1190000020680168)

# Spring

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
    

# 编程基础

- [正则表达式的环视](https://blog.csdn.net/lxcnn/article/details/4304754)

    > `str.replaceFirst("(?<=.{5}).+", "...")`
    >
    > 保留 `str` 的前五位字符，其余字符用 `...` 代替
    
    - [正则表达式参考文档](http://notes.tanchuanqi.com/tools/regex.html)