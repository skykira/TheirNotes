## ConcurrentHashMap 源码分析

### 常量

```java
private static final int MAXIMUM_CAPACITY = 1 << 30;
private static final int DEFAULT_CAPACITY = 16;
 
// 下面3个，在1.8的HashMap中也有相同的常量
 
// 一个hash桶中hash冲突的数目大于此值时，把链表转化为红黑树，加快hash冲突时的查找速度
static final int TREEIFY_THRESHOLD = 8;
 
// 一个hash桶中hash冲突的数目小于等于此值时，把红黑树转化为链表，当数目比较少时，链表的实际查找速度更快，也是为了查找效率
static final int UNTREEIFY_THRESHOLD = 6;
 
// 当table数组的长度小于此值时，不会把链表转化为红黑树。所以转化为红黑树有两个条件，还有一个是 TREEIFY_THRESHOLD
static final int MIN_TREEIFY_CAPACITY = 64;
 
// 虚拟机限制的最大数组长度，在ArrayList中有说过，jdk1.8新引入的，ConcurrentHashMap的主体代码中是不使用这个的，主要用在Collection.toArray两个方法中
static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
 
// 默认并行级别，主体代码中未使用此常量，为了兼容性，保留了之前的定义，主要是配合同样是为了兼容性的Segment使用，另外在构造方法中有一些作用
// 千万注意，1.8的并发级别有了大的改动，具体并发级别可以认为是hash桶是数量，也就是容量，会随扩容而改变，不再是固定值
private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
 
// 加载因子，为了兼容性，保留了这个常量（名字变了），配合同样是为了兼容性的Segment使用
// 1.8的ConcurrentHashMap的加载因子固定为 0.75，构造方法中指定的参数是不会被用作loadFactor的，为了计算方便，统一使用 n - (n >> 2) 代替浮点乘法 *0.75
private static final float LOAD_FACTOR = 0.75f;
 
// 扩容操作中，transfer这个步骤是允许多线程的，这个常量表示一个线程执行transfer时，最少要对连续的16个hash桶进行transfer
//     （不足16就按16算，多控制下正负号就行）
// 也就是单线程执行transfer时的最小任务量，单位为一个hash桶，这就是线程的transfer的步进（stride）
// 最小值是DEFAULT_CAPACITY，不使用太小的值，避免太小的值引起transfer时线程竞争过多，如果计算出来的值小于此值，就使用此值
// 正常步骤中会根据CPU核心数目来算出实际的，一个核心允许8个线程并发执行扩容操作的transfer步骤，这个8是个经验值，不能调整的
// 因为transfer操作不是IO操作，也不是死循环那种100%的CPU计算，CPU计算率中等，1核心允许8个线程并发完成扩容，理想情况下也算是比较合理的值
// 一段代码的IO操作越多，1核心对应的线程就要相应设置多点，CPU计算越多，1核心对应的线程就要相应设置少一些
// 表明：默认的容量是16，也就是默认构造的实例，第一次扩容实际上是单线程执行的，看上去是可以多线程并发（方法允许多个线程进入），
//     但是实际上其余的线程都会被一些if判断拦截掉，不会真正去执行扩容
private static final int MIN_TRANSFER_STRIDE = 16;
 
// 用于生成每次扩容都唯一的生成戳的数，最小是6。很奇怪，这个值不是常量，但是也不提供修改方法。
/** The number of bits used for generation stamp in sizeCtl. Must be at least 6 for 32bit arrays. */
private static int RESIZE_STAMP_BITS = 16;
 
// 最大的扩容线程的数量，如果上面的 RESIZE_STAMP_BITS = 32，那么此值为 0，这一点也很奇怪。
/** The maximum number of threads that can help resize. Must fit in 32 - RESIZE_STAMP_BITS bits. */
private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;
 
// 移位量，把生成戳移位后保存在sizeCtl中当做扩容线程计数的基数，相反方向移位后能够反解出生成戳
/** The bit shift for recording size stamp in sizeCtl. */
private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
 
// 下面几个是特殊的节点的hash值，正常节点的hash值在hash函数中都处理过了，不会出现负数的情况，特殊节点在各自的实现类中有特殊的遍历方法
// ForwardingNode的hash值，ForwardingNode是一种临时节点，在扩进行中才会出现，并且它不存储实际的数据
// 如果旧数组的一个hash桶中全部的节点都迁移到新数组中，旧数组就在这个hash桶中放置一个ForwardingNode
// 读操作或者迭代读时碰到ForwardingNode时，将操作转发到扩容后的新的table数组上去执行，写操作碰见它时，则尝试帮助扩容
/** Encodings for Node hash fields. See above for explanation. */
static final int MOVED     = -1; // hash for forwarding nodes
 
// TreeBin的hash值，TreeBin是ConcurrentHashMap中用于代理操作TreeNode的特殊节点，持有存储实际数据的红黑树的根节点
// 因为红黑树进行写入操作，整个树的结构可能会有很大的变化，这个对读线程有很大的影响，
//     所以TreeBin还要维护一个简单读写锁，这是相对HashMap，这个类新引入这种特殊节点的重要原因
static final int TREEBIN   = -2; // hash for roots of trees
 
// ReservationNode的hash值，ReservationNode是一个保留节点，就是个占位符，不会保存实际的数据，正常情况是不会出现的，
// 在jdk1.8新的函数式有关的两个方法computeIfAbsent和compute中才会出现
static final int RESERVED  = -3; // hash for transient reservations
 
// 用于和负数hash值进行 & 运算，将其转化为正数（绝对值不相等），Hashtable中定位hash桶也有使用这种方式来进行负数转正数
static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash
 
// CPU的核心数，用于在扩容时计算一个线程一次要干多少活
/** Number of CPUS, to place bounds on some sizings */
static final int NCPU = Runtime.getRuntime().availableProcessors();
 
// 在序列化时使用，这是为了兼容以前的版本
/** For serialization compatibility. */
private static final ObjectStreamField[] serialPersistentFields = {
    new ObjectStreamField("segments", Segment[].class),
    new ObjectStreamField("segmentMask", Integer.TYPE),
    new ObjectStreamField("segmentShift", Integer.TYPE)
};
```

### 变量

```java
transient volatile Node<K,V>[] table;
private transient KeySetView<K,V> keySet;
private transient ValuesView<K,V> values;
private transient EntrySetView<K,V> entrySet;
 
// 扩容后的新的table数组，只有在扩容时才有用
// nextTable != null，说明扩容方法还没有真正退出，一般可以认为是此时还有线程正在进行扩容，
//     极端情况需要考虑此时扩容操作只差最后给几个变量赋值（包括nextTable = null）的这个大的步骤，
//     这个大步骤执行时，通过sizeCtl经过一些计算得出来的扩容线程的数量是0
private transient volatile Node<K,V>[] nextTable;
 
/*
1. 新建未初始化时，sizeCtl 用于暂存初始容量大小
2. 初始化时，值为 -1，表示当前集合正在被初始化，其他线程发现该值为 -1 时会让出CPU资源以便初始化操作尽快完成
3. 初始化完成后，sizeCtl 用于记录当前集合的 threshold
4. 扩容时，sizeCtl 用于存储 resizeStamp 和 当前扩容的并发线程数。此时 sizeCtl 的值被置为：((rs << RESIZE_STAMP_SHIFT) + 2) + (正在扩容的线程数)。对容量为16的集合单线程扩容时，sizeCtl 十进制表示为 -2145714174。

   Integer.numberOfLeadingZeros(16) | (1 << (RESIZE_STAMP_BITS - 1)) = 32795，二进制表示为 1000 0000 0001 1011。(rs << RESIZE_STAMP_SHIFT) + 2 = -2145714174，补码为 1000 0000 0001 1011 0000 0000 0000 0010
*/
private transient volatile int sizeCtl;
 
// 下一个transfer任务的起始下标index 加上1 之后的值，transfer时下标index从length - 1开始往0走
// transfer时方向是倒过来的，迭代时是下标从小往大，二者方向相反，尽量减少扩容时transefer和迭代两者同时处理一个hash桶的情况，
// 顺序相反时，二者相遇过后，迭代没处理的都是已经transfer的hash桶，transfer没处理的，都是已经迭代的hash桶，冲突会变少
// 下标在[nextIndex - 实际的stride （下界要 >= 0）, nextIndex - 1]内的hash桶，就是每个transfer的任务区间
// 每次接受一个transfer任务，都要CAS执行 transferIndex = transferIndex - 实际的stride，
//     保证一个transfer任务不会被几个线程同时获取（相当于任务队列的size减1）
// 当没有线程正在执行transfer任务时，一定有transferIndex <= 0，这是判断是否需要帮助扩容的重要条件（相当于任务队列为空）
private transient volatile int transferIndex;
 
// 下面三个主要与统计数目有关，可以参考jdk1.8新引入的java.util.concurrent.atomic.LongAdder的源码，帮助理解
// 计数器基本值，主要在没有碰到多线程竞争时使用，需要通过CAS进行更新
private transient volatile long baseCount;
 
// CAS自旋锁标志位，用于初始化，或者counterCells扩容时
private transient volatile int cellsBusy;
 
// 用于高并发的计数单元，如果初始化了这些计数单元，那么跟table数组一样，长度必须是2^n的形式
private transient volatile CounterCell[] counterCells;
```

### 添加元素

```java
    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        //作为后面判断是否要检查扩容、是否要树化的判断标志位
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh; K fk; V fv;
            if (tab == null || (n = tab.length) == 0)
                //未初始化，则先初始化
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                //hash桶还是空的，直接插入
                if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                //遇见ForwardingNode(转发节点), 帮助一起扩容
                tab = helpTransfer(tab, f);
            else if (onlyIfAbsent // check first node without acquiring lock
                     && fh == hash
                     && ((fk = f.key) == key || (fk != null && key.equals(fk)))
                     && (fv = f.val) != null)
                return fv;
            else {
                //开始遍历寻找合适的插入/替换位置
                V oldVal = null;
                synchronized (f) {
                    //锁住第一个节点
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            //普通链表节点
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                //循环遍历查找合适位置
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key, value);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            //树节点
                            Node<K,V> p;
                            //设置为2，标识后面一定会检查是否要扩容
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                        else if (f instanceof ReservationNode)
                            //computeIfAbsent时使用
                            throw new IllegalStateException("Recursive update");
                    }
                }
                if (binCount != 0) {
                    //链表长度 > 树化阈值时，树化
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        // 计数，并检查是否需要扩容
        addCount(1L, binCount);
        return null;
    }

    private final void addCount(long x, int check) {
        CounterCell[] cs; long b, s;
        //LongAdder 类计数（高并发时性能优越）
        if ((cs = counterCells) != null ||
            !U.compareAndSetLong(this, BASECOUNT, b = baseCount, s = b + x)) {
            CounterCell c; long v; int m;
            boolean uncontended = true;
            if (cs == null || (m = cs.length - 1) < 0 ||
                (c = cs[ThreadLocalRandom.getProbe() & m]) == null ||
                !(uncontended =
                  U.compareAndSetLong(c, CELLVALUE, v = c.value, v + x))) {
                fullAddCount(x, uncontended);
                return;
            }
            //check（binCount）<= 1 代表未增加新节点，无需扩容
            if (check <= 1)
                return;
            //计算总数
            s = sumCount();
        }
        if (check >= 0) {
            Node<K,V>[] tab, nt; int n, sc;
            //如果 s > sizeCtl, 也就是达到了扩容阈值，需要扩容了
            while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
                   (n = tab.length) < MAXIMUM_CAPACITY) {
                //标识当前容量代表的扩容戳，高15位代表当前的容量，最高一位为1，低16位代表并行线程数+2
                int rs = resizeStamp(n);
                if (sc < 0) {
                    //sc<0，扩容必然已经开始了   
                    //sc 高16位存储的扩容戳与当前扩容戳不一致||扩容已经结束||扩容线程数达到最大||第一个线程还没初始化完毕或者扩容刚结束||扩容工作都被分配完了，此时当前线程统统退出扩容

                    //扩容线程每退出一条，sc 便减一。一开始只有一条扩容线程时，初始值为(rs << RESIZE_STAMP_SHIFT) + 2，所以sc == (rs << RESIZE_STAMP_SHIFT) + 1)时，表示当前扩容戳对应的扩容已经结束了。
                    //sc == rs + 1 || sc == rs + MAX_RESIZERS，该bug在高版本被修复了
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                        transferIndex <= 0)
                        break;
                    if (U.compareAndSetInt(this, SIZECTL, sc, sc + 1))
                    //还在扩容中，帮忙扩
                        transfer(tab, nt);
                }
                else if (U.compareAndSetInt(this, SIZECTL, sc,
                                             (rs << RESIZE_STAMP_SHIFT) + 2))
                    //没有其他线程扩容，自己开始搞
                    transfer(tab, null);
                s = sumCount();
            }
        }
    }

    private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
        int n = tab.length, stride;
        //计算扩容步长
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE; // subdivide range
        //初始化 nextTable，当 nextTable 初始化完毕，不为 null 后，其余线程才被允许进来帮忙
        if (nextTab == null) {            // initiating
            try {
                @SuppressWarnings("unchecked")
                Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;
            //transferIndex初始位置在最右侧的桶
            transferIndex = n;
        }
        int nextn = nextTab.length;
        //转发节点，hash == -1，不存储数据，持有新的 Node 数组
        //作用：1. 占位，标识数组该位置的桶已经迁移完毕。2. 将查询操作转发到新 Node 数组
        ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
        //循环标志，标识是否需要继续处理下一个桶
        boolean advance = true;
        //扩容结束标志
        boolean finishing = false; // to ensure sweep before committing nextTab
        for (int i = 0, bound = 0;;) {
            Node<K,V> f; int fh;
            while (advance) {
                //每次通过该循环拿到需要迁移的桶
                int nextIndex, nextBound;

                if (--i >= bound || finishing)
                    advance = false;
                else if ((nextIndex = transferIndex) <= 0) {
                    i = -1;
                    advance = false;
                }
                else if (U.compareAndSetInt
                         (this, TRANSFERINDEX, nextIndex,
                          nextBound = (nextIndex > stride ?
                                       nextIndex - stride : 0))) {
                    bound = nextBound;
                    i = nextIndex - 1;
                    advance = false;
                }
            }
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                if (finishing) {
                    nextTable = null;
                    table = nextTab;
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                if (U.compareAndSetInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        return;
                    finishing = advance = true;
                    i = n; // recheck before commit
                }
            }
            else if ((f = tabAt(tab, i)) == null)
                advance = casTabAt(tab, i, null, fwd);
            else if ((fh = f.hash) == MOVED)
                advance = true; // already processed
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        Node<K,V> ln, hn;
                        if (fh >= 0) {
                            int runBit = fh & n;
                            Node<K,V> lastRun = f;
                            for (Node<K,V> p = f.next; p != null; p = p.next) {
                                int b = p.hash & n;
                                if (b != runBit) {
                                    runBit = b;
                                    lastRun = p;
                                }
                            }
                            if (runBit == 0) {
                                ln = lastRun;
                                hn = null;
                            }
                            else {
                                hn = lastRun;
                                ln = null;
                            }
                            for (Node<K,V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash; K pk = p.key; V pv = p.val;
                                if ((ph & n) == 0)
                                    ln = new Node<K,V>(ph, pk, pv, ln);
                                else
                                    hn = new Node<K,V>(ph, pk, pv, hn);
                            }
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                        else if (f instanceof TreeBin) {
                            TreeBin<K,V> t = (TreeBin<K,V>)f;
                            TreeNode<K,V> lo = null, loTail = null;
                            TreeNode<K,V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            for (Node<K,V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                TreeNode<K,V> p = new TreeNode<K,V>
                                    (h, e.key, e.val, null, null);
                                if ((h & n) == 0) {
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                }
                                else {
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                (hc != 0) ? new TreeBin<K,V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                (lc != 0) ? new TreeBin<K,V>(hi) : t;
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                    }
                }
            }
        }
    }
```

### TreeBin

```java
// 红黑树节点TreeNode实际上还保存有链表的指针，因此也可以用链表的方式进行遍历读取操作
// 自身维护一个简单的读写锁，不用考虑写-写竞争的情况
// 不是全部的写操作都要加写锁，只有部分的put/remove需要加写锁
// 很多方法的实现和jdk1.8的ConcurrentHashMap.TreeNode里面的方法基本一样，可以互相参考
static final class TreeBin<K,V> extends Node<K,V> {
    TreeNode<K,V> root; // 红黑树结构的跟节点
    volatile TreeNode<K,V> first; // 链表结构的头节点
    volatile Thread waiter; // 最近的一个设置 WAITER 标识位的线程
    volatile int lockState; // 整体的锁状态标识位
 
    // values for lockState
    // 二进制001，红黑树的 写锁状态
    static final int WRITER = 1; // set while holding write lock
    // 二进制010，红黑树的 等待获取写锁的状态，中文名字太长，后面用 WAITER 代替
    static final int WAITER = 2; // set when waiting for write lock
    // 二进制100，红黑树的 读锁状态，读锁可以叠加，也就是红黑树方式可以并发读，每有一个这样的读线程，lockState都加上一个READER的值
    static final int READER = 4; // increment value for setting read lock
 
    // 重要的一点，红黑树的 读锁状态 和 写锁状态 是互斥的，但是从ConcurrentHashMap角度来说，读写操作实际上可以是不互斥的
    // 红黑树的 读、写锁状态 是互斥的，指的是以红黑树方式进行的读操作和写操作（只有部分的put/remove需要加写锁）是互斥的
    // 但是当有线程持有红黑树的 写锁 时，读线程不会以红黑树方式进行读取操作，而是使用简单的链表方式进行读取，此时读操作和写操作可以并发执行
    // 当有线程持有红黑树的 读锁 时，写线程可能会阻塞，不过因为红黑树的查找很快，写线程阻塞的时间很短
    // 另外一点，ConcurrentHashMap的put/remove/replace方法本身就会锁住TreeBin节点，这里不会出现写-写竞争的情况，因此这里的读写锁可以实现得很简单
 
    // 在hashCode相等并且不是Comparable类时才使用此方法进行判断大小
    static int tieBreakOrder(Object a, Object b) {
        int d;
        if (a == null || b == null || (d = a.getClass().getName().compareTo(b.getClass().getName())) == 0)
            d = (System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1);
        return d;
    }
 
    // 用以b为头结点的链表创建一棵红黑树
    TreeBin(TreeNode<K,V> b) {
        super(TREEBIN, null, null, null);
        this.first = b;
        TreeNode<K,V> r = null;
        for (TreeNode<K,V> x = b, next; x != null; x = next) {
            next = (TreeNode<K,V>)x.next;
            x.left = x.right = null;
            if (r == null) {
                x.parent = null;
                x.red = false;
                r = x;
            }
            else {
                K k = x.key;
                int h = x.hash;
                Class<?> kc = null;
                for (TreeNode<K,V> p = r;;) {
                    int dir, ph;
                    K pk = p.key;
                    if ((ph = p.hash) > h)
                        dir = -1;
                    else if (ph < h)
                        dir = 1;
                    else if ((kc == null && (kc = comparableClassFor(k)) == null) || (dir = compareComparables(kc, k, pk)) == 0)
                        dir = tieBreakOrder(k, pk);
                        TreeNode<K,V> xp = p;
                    if ((p = (dir <= 0) ? p.left : p.right) == null) {
                        x.parent = xp;
                        if (dir <= 0)
                            xp.left = x;
                        else
                            xp.right = x;
                        r = balanceInsertion(r, x);
                        break;
                    }
                }
            }
        }
        this.root = r;
        assert checkInvariants(root);
    }
 
    /**
     * Acquires write lock for tree restructuring.
     */
    // 对根节点加 写锁，红黑树重构时需要加上 写锁
    private final void lockRoot() {
        if (!U.compareAndSwapInt(this, LOCKSTATE, 0, WRITER)) // 先尝试获取一次 写锁
            contendedLock(); // offload to separate method 单独抽象出一个方法，直到获取到 写锁 这个调用才会返回
    }
 
    // 释放 写锁
    private final void unlockRoot() {
        lockState = 0;
    }

    //竞争写锁
    private final void contendedLock() {
        boolean waiting = false;
        for (int s;;) {
            if (((s = lockState) & ~WAITER) == 0) {
                //s(锁当前状态)仅有 WAITER 时，尝试获取写锁（自己与自己的取反相 & 才为 0）
                if (U.compareAndSwapInt(this, LOCKSTATE, s, WRITER)) {
                    if (waiting) 
                        waiter = null;
                    return;
                }
            }
            
            else if ((s & WAITER) == 0) {
                //s 状态中没有 WAITER 时，尝试将等待状态写入 lockState 中
                if (U.compareAndSwapInt(this, LOCKSTATE, s, s | WAITER)) { // 尝试占据 WAITER 状态标识位
                    waiting = true; // 表明自己正处于 WAITER 状态，并且让下一个被用于进入下一个 else if
                    waiter = Thread.currentThread();
                }
            }
            else if (waiting)
            //s 已被标记为等待状态，但它还持有读锁，阻塞当前线程吧，累了
                LockSupport.park(this); // 阻塞自己
        }
    }
 
    // 从根节点开始遍历查找，找到“相等”的节点就返回它，没找到就返回null
    // 当有写线程加上 写锁 时，使用链表方式进行查找
    final Node<K,V> find(int h, Object k) {
        if (k != null) {
            for (Node<K,V> e = first; e != null; ) {
                int s; K ek;
                // 两种特殊情况下以链表的方式进行查找
                // 1、有线程正持有 写锁，这样做能够不阻塞读线程
                // 2、有线程 WAITER 时，不再继续加 读锁，能够让已经被阻塞的写线程尽快恢复运行，或者刚好让某个写线程不被阻塞
                if (((s = lockState) & (WAITER|WRITER)) != 0) {
                    if (e.hash == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    e = e.next;
                }
                else if (U.compareAndSwapInt(this, LOCKSTATE, s, s + READER)) { // 读线程数量加1，读状态进行累加
                    TreeNode<K,V> r, p;
                    try {
                        p = ((r = root) == null ? null : r.findTreeNode(h, k, null));
                    } finally {
                        Thread w;
                        // 如果这是最后一个读线程，并且有写线程因为 读锁 而阻塞，那么要通知它，告诉它可以尝试获取写锁了
                        // U.getAndAddInt(this, LOCKSTATE, -READER)这个操作是在更新之后返回lockstate的旧值，
                        //     不是返回新值，相当于先判断==，再执行减法
                        if (U.getAndAddInt(this, LOCKSTATE, -READER) == (READER|WAITER) && (w = waiter) != null)
                            LockSupport.unpark(w); // 让被阻塞的写线程运行起来，重新去尝试获取 写锁
                    }
                    return p;
                }
            }
        }
        return null;
    }
 
    // 用于实现ConcurrentHashMap.putVal
    final TreeNode<K,V> putTreeVal(int h, K k, V v) {
        Class<?> kc = null;
        boolean searched = false;
        for (TreeNode<K,V> p = root;;) {
            int dir, ph; K pk;
            if (p == null) {
                first = root = new TreeNode<K,V>(h, k, v, null, null);
                break;
            }
            else if ((ph = p.hash) > h)
                dir = -1;
            else if (ph < h)
                dir = 1;
            else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                return p;
            else if ((kc == null && (kc = comparableClassFor(k)) == null) || (dir = compareComparables(kc, k, pk)) == 0) {
                if (!searched) {
                    TreeNode<K,V> q, ch;
                    searched = true;
                    if (((ch = p.left) != null && (q = ch.findTreeNode(h, k, kc)) != null) ||
                        ((ch = p.right) != null && (q = ch.findTreeNode(h, k, kc)) != null))
                        return q;
                }
                dir = tieBreakOrder(k, pk);
            }
 
            TreeNode<K,V> xp = p;
            if ((p = (dir <= 0) ? p.left : p.right) == null) {
                TreeNode<K,V> x, f = first;
                first = x = new TreeNode<K,V>(h, k, v, f, xp);
                if (f != null)
                    f.prev = x;
                if (dir <= 0)
                    xp.left = x;
                else
                    xp.right = x;
                // 下面是有关put加 写锁 部分
                // 二叉搜索树新添加的节点，都是取代原来某个的NIL节点（空节点，null节点）的位置
                if (!xp.red) // xp是新添加的节点的父节点，如果它是黑色的，新添加一个红色节点就能够保证x这部分的一部分路径关系不变，
                             //     这是insert重新染色的最最简单的情况
                    x.red = true; // 因为这种情况就是在树的某个末端添加节点，不会改变树的整体结构，对读线程使用红黑树搜索的搜索路径没影响
                else { // 其他情况下会有树的旋转的情况出现，当读线程使用红黑树方式进行查找时，可能会因为树的旋转，导致多遍历、少遍历节点，影响find的结果
                    lockRoot(); // 除了那种最最简单的情况(只改动颜色)，其余的都要加 写锁，让读线程用链表方式进行遍历读取
                    try {
                        root = balanceInsertion(root, x);
                    } finally {
                        unlockRoot();
                    }
                }
                break;
            }
        }
        assert checkInvariants(root);
        return null;
    }
 
    /*
    基本是同jdk1.8的HashMap.TreeNode.removeTreeNode，仍然是从链表以及红黑树上都删除节点
    
    两点区别：
    1、返回值，红黑树的规模太小时，返回true，调用者再去进行树->链表的转化；
    2、红黑树规模足够，不用变换成链表时，进行红黑树上的删除要加 写锁
    
    比普通红黑树删除更复杂的点在于，不能与 next 指针指向的节点交换，因为可能有读线程在使用。
    */
    final boolean removeTreeNode(TreeNode<K,V> p) {
        TreeNode<K,V> next = (TreeNode<K,V>)p.next;
        TreeNode<K,V> pred = p.prev;  // unlink traversal pointers
        TreeNode<K,V> r, rl;
        if (pred == null)
            first = next;
        else
            pred.next = next;
        if (next != null)
            next.prev = pred;
        if (first == null) {
            root = null;
            return true;
        }
        if ((r = root) == null || r.right == null || (rl = r.left) == null || rl.left == null) // too small
            return true;
        lockRoot();
        try {
            TreeNode<K,V> replacement;
            TreeNode<K,V> pl = p.left;
            TreeNode<K,V> pr = p.right;
            if (pl != null && pr != null) {
                TreeNode<K,V> s = pr, sl;
                while ((sl = s.left) != null) // find successor
                    s = sl;
                boolean c = s.red; s.red = p.red; p.red = c; // swap colors
                TreeNode<K,V> sr = s.right;
                TreeNode<K,V> pp = p.parent;
                if (s == pr) { // p was s's direct parent
                    p.parent = s;
                    s.right = p;
                }
                else {
                    TreeNode<K,V> sp = s.parent;
                    if ((p.parent = sp) != null) {
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    }
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;
                if ((s.left = pl) != null)
                    pl.parent = s;
                if ((s.parent = pp) == null)
                    r = s;
                else if (p == pp.left)
                    pp.left = s;
                else
                    pp.right = s;
                if (sr != null)
                    replacement = sr;
                else
                    replacement = p;
            }
            else if (pl != null)
                replacement = pl;
            else if (pr != null)
                replacement = pr;
            else
                replacement = p;
            if (replacement != p) {
                TreeNode<K,V> pp = replacement.parent = p.parent;
                if (pp == null)
                    r = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;
            }
 
            root = (p.red) ? r : balanceDeletion(r, replacement);
 
            if (p == replacement) {  // detach pointers
                TreeNode<K,V> pp;
                if ((pp = p.parent) != null) {
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
                    p.parent = null;
                }
            }
        } finally {
            unlockRoot();
        }
        assert checkInvariants(root);
        return false;
    }
 
    // 下面四个是经典的红黑树方法，改编自《算法导论》
    static <K,V> TreeNode<K,V> rotateLeft(TreeNode<K,V> root, TreeNode<K,V> p);
    static <K,V> TreeNode<K,V> rotateRight(TreeNode<K,V> root, TreeNode<K,V> p);
    static <K,V> TreeNode<K,V> balanceInsertion(TreeNode<K,V> root, TreeNode<K,V> x);
    static <K,V> TreeNode<K,V> balanceDeletion(TreeNode<K,V> root, TreeNode<K,V> x);
    // 递归检查一些关系，确保构造的是正确无误的红黑树
    static <K,V> boolean checkInvariants(TreeNode<K,V> t);
    // Unsafe相关的初始化工作
    private static final sun.misc.Unsafe U;
    private static final long LOCKSTATE;
    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = TreeBin.class;
            LOCKSTATE = U.objectFieldOffset(k.getDeclaredField("lockState"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
```

### 