# Java基础

- [`Class.this` 与 `this` 的区别](https://stackoverflow.com/questions/5666134/what-is-the-difference-between-class-this-and-this-in-java)

- [`toArray()` 与 `toArray(new String[0])`](https://stackoverflow.com/questions/18136437/whats-the-use-of-new-string0-in-toarraynew-string0)

# Java 并发

- [对象计算 hashcode 将导致偏向锁膨胀](https://blog.csdn.net/P19777/article/details/103125545)

- [偏向锁的批量重偏向与批量撤销](https://www.cnblogs.com/LemonFive/p/11248248.html)

    未达到批量重偏向阈值时，偏向锁膨胀为轻量级锁；达到批量重偏向阈值时，锁对象才会进行重偏向操作，且重偏向只能有一次。

- [Java 中 `volatile` 的语义](https://www.jianshu.com/p/4e59476963b0)

- [Java中的偏向锁、轻量级锁、重量级锁解析](https://blog.csdn.net/lengxiao1993/article/details/81568130?depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1&utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1)

- [Java中的锁降级✨✨](http://openjdk.java.net/jeps/8183909)

    该优化提案显示，目前，实现重量级锁的 `monitor` 对象可以在STW时被清除，清除的是那些只有 `VM Thread`会去访问它们的 `monitor` 对象（也就是，不再使用的 `monitor` 对象）。

    因此，重量级锁可以降级为轻量级锁。

# Java 安全

- [如何理解恶意代码执行 `AccessController.doPrivileged()`](https://stackoverflow.com/questions/37962070/malicious-code-running-accesscontroller-doprivileged)

# 函数式编程

- [`BiConsumer` 为什么可以引用仅有一个参数的方法](https://stackoverflow.com/questions/58046693/biconsumer-and-method-reference-of-one-parameter)

# JVM

- [图说 Java 字节码指令](https://segmentfault.com/a/1190000008606277)

- [Java 编译器优化和运行期优化概览](https://blog.csdn.net/u013305783/article/details/81279175)

- [`Thread-Local Handshakes` 优化安全点STW](https://openjdk.java.net/jeps/312)

    对象的偏向锁撤销操作也可以无需程序运行到 `SafePoint`



