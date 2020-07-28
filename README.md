<!-- TOC -->

- [Java 基础](#java-基础)
  - [HashMap](#hashmap)
  - [ConcurrentHashMap](#concurrenthashmap)
- [Java 并发](#java-并发)
- [Java IO](#java-io)
- [Java 安全](#java-安全)
- [函数式编程](#函数式编程)
- [JVM](#jvm)
  - [字节码操作](#字节码操作)
  - [调优](#调优)
- [分布式](#分布式)
  - [Raft](#raft)
  - [BFT](#bft)
- [DateBase](#datebase)
- [Spring](#spring)
  - [源码解析](#源码解析)
  - [关键组件](#关键组件)
    - [`PostProcessor` bean 后置处理器](#postprocessor-bean-后置处理器)
- [Dubbo](#dubbo)
- [Tomcat](#tomcat)
- [Netty](#netty)
- [编程基础](#编程基础)
- [数据结构](#数据结构)

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

- [Java 动态代理](https://github.com/skykira/TheirNotes/tree/master/SourceCode/JDK%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86)

    - [JDK 自带动态代理源码分析](https://blog.csdn.net/weixin_43217817/article/details/102268504)

    > JDK 操作字节码生成所需要的代理类 `Class`，该代理类继承了 `Proxy` 并实现了我们的接口。Proxy 类实例提供了保存 `InvocationHandler` 自定义逻辑的功能。
    >
    > 代理类中所有的方法（包括类似 `toString()`）都通过我们自定义的 `InvocationHandler` 的 `invoke` 方法来实现，因此单个代理类的方法会被加入同样的逻辑。

    - [CGLib 动态代理](https://dannashen.github.io/2019/05/09/Cglib%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86/)

        拦截器中对被拦截方法的调用通过 `proxy.invokeSuper(obj, args);` 完成，相当于子类直接调用父类，比 `invokeHandler` 的反射调用快些，量大的话。

- [检查型异常与非检查型异常](https://blog.csdn.net/u013630349/article/details/50850880)

## HashMap

- [JDK1.7 HashMap infinite loop](https://my.oschina.net/u/1024107/blog/758588)

- [HashMap 删除节点时的树退化为链表](https://www.cnblogs.com/lifacheng/p/11032482.html)

## ConcurrentHashMap

- [ConcurrentHashMap 源码分析](https://blog.csdn.net/u011392897/article/details/60479937)

- [ConcurrentHashMap 扩容图文详解](https://blog.csdn.net/ZOKEKAI/article/details/90051567)

    正在迁移的hash桶遇到 get 操作会发生什么？

    答：在扩容过程期间形成的 hn 和 ln 链是使用的类似于复制引用的方式，也就是说 ln 和 hn 链是复制出来的，而非原来的链表迁移过去的，所以原来 hash 桶上的链表并没有受到影响，因此从迁移开始到迁移结束这段时间都是可以正常访问原数组 hash 桶上面的链表，迁移结束后放置上fwd，往后的访问请求就直接转发到扩容后的数组去了。

- [ConcurrentHashMap 方法总结](https://juejin.im/post/5b001639f265da0b8f62d0f8#comment)

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

# Java IO

- Linux IO 模型
  - [简述 Linux IO 模型](https://mp.weixin.qq.com/s/3C7Iv1jof8jitOPL_4c_bQ)
  - [详述 Linux IO 模型](https://www.jianshu.com/p/486b0965c296)

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

- [Full GC 时新生代的垃圾收集方式](https://www.zhihu.com/question/62604570/answer/201129934)

    CMS 之前的垃圾收集器并不能单独收集老年代，Full GC 对整个堆使用 `MSC(Mark-Sweep-Compact)` 算法。

- [Major GC 和 Full GC 的区别](https://www.zhihu.com/question/41922036/answer/93079526)

- [ParallelScavenge 的 fullGC](https://www.zhihu.com/question/48780091/answer/113063216)

- [GCLocker-initiated young GC 多余发生](https://www.jianshu.com/p/ecc57a81f73c)

- [理解 TLAB](https://www.jianshu.com/p/2343f2c0ecc4)

## 字节码操作

- [理解字节码增强工具包 Instrumentation ](https://www.throwable.club/2019/06/29/java-understand-instrument-first/)

    包括 `premain` 和 `agentmain` 的使用

- [Java 字节码增强技术](https://tech.meituan.com/2019/09/05/java-bytecode-enhancement.html)


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

- [数据库事务中的一致性](https://www.zhihu.com/question/31346392)

- [Innodb 双写防止 `partial page write`](https://blog.csdn.net/jolly10/article/details/79791574)

- [ICP 索引下推](https://database.51cto.com/art/201907/599968.htm)

    针对辅助索引能覆盖到的列，将 where 条件的判断下推到存储引擎层。

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

- [`$$` 与 `<generated>` 代表的含义](https://stackoverflow.com/questions/33605246/what-does-and-generated-means-in-java-stacktrace)

## 源码解析

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

    

## 关键组件

### `PostProcessor` bean 后置处理器

- [`AbstractAdvisorAutoProxyCreator` 决定是否要对当前 bean 进行代理](https://blog.csdn.net/z69183787/article/details/83311522)

    > spring 依赖注入时，什么时候会创建代理类，什么时候是普通 bean？

# Dubbo

- [接口自适应类 T$Adaptive 查看](https://blog.csdn.net/swordyijianpku/article/details/105737163?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2)

# Tomcat

- [Tomcat 处理请求过程源码解析](https://blog.csdn.net/leileibest_437147623/article/details/85287568?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase)

# Netty

> Reactor 模式
> 
> Reactor模式是一个事件驱动，用于一种处理一个或多个客户端并发进行服务请求的设计模式。
> 
> 它将服务端接收请求与事件处理分离，提高了系统处理并发的能力，`java NIO 的 reactor 模式是基于系统内核的多路复用技术实现的`。

- [SocketChannel 与 ServerSocketChannel 区别](https://blog.csdn.net/hzmlg1988/article/details/88082492)

- [Netty 启动源码分析](https://mp.weixin.qq.com/mp/appmsgalbum?action=getalbum&album_id=1342147420482011137&__biz=MzI2NzY4MjM1OQ==#wechat_redirect)(猿灯塔|需微信中打开)

# 编程基础

- [正则表达式的环视](https://blog.csdn.net/lxcnn/article/details/4304754)

    > `str.replaceFirst("(?<=.{5}).+", "...")`
    >
    > 保留 `str` 的前五位字符，其余字符用 `...` 代替
    
    - [正则表达式参考文档](http://notes.tanchuanqi.com/tools/regex.html)

- [TCP/IP 三次握手思考](https://blog.csdn.net/lengxiao1993/article/details/82771768?utm_medium=distribute.wap_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.wap_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase)

- [TCP/IP 四次挥手](https://blog.csdn.net/ThinkWon/article/details/104903925?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-3.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-3.nonecase)

- 将 javassist 动态生成的类打印出来

    `(ClassGenerator)ccp.getClassPool().get("com.alibaba.dubbo.common.bytecode.Proxy0").debugWriteFile()`

# 数据结构

- [Append-only B+ Tree](https://blog.csdn.net/lpstudy/article/details/83722007)

- [LevelDB 设计与实现 —— LSM tree](https://blog.csdn.net/anderscloud/article/details/7182165)

- [红黑树工具](https://rbtree.phpisfuture.com/)