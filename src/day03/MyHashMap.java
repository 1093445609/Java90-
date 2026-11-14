package src.day03;

/**
 * ============================================================
 *  MyHashMap —— 手写迷你版 HashMap，通过 Day03 全部验收测试
 * ============================================================
 *
 * 【HashMap 到底是个什么东西？】
 * 想象你有一排抽屉（数组），编号 0~15，共 16 个格子。要存 "张三"->90 分：
 *   1. 把 key 丢进 hashCode() 机器，吐出整数（Java 每个对象都有 hashCode）
 *   2. 用位运算压缩到 0~15：index = hash & (16-1)   ← 叫"取模定位"
 *      （容量是 2 的幂时，& (n-1) 等价于 % n，但快得多）
 *   3. 塞进那个抽屉。取的时候重复三步，O(1)，超快。
 *
 * 【坑一：两个 key 算出同一个 index 怎么办？——哈希冲突】
 *   抽屉里不存一个东西，而是挂一条链表。新来的往链表上插（尾插法），
 *   找的时候顺着链表用 equals() 比对 key。数组负责快速定位，链表负责兜底冲突。
 *
 * 【坑二：链表太长退化成 O(n) 怎么办？——扩容 rehash】
 *   设负载因子 0.75：16 个抽屉装到 12 个就开始挤了。再塞之前先把数组翻倍
 *   （16->32），然后把旧数组里所有节点重新算 index 搬到新数组。
 *   翻倍后原来挤一起的节点有一半会挪到"高位区"，链表瞬间变短。
 *
 * 【坑三：hashCode 分布不均怎么办？——扰动函数】
 *   有些类的 hashCode 只在低位变化、高位全 0。只用低位做 & 运算，
 *   高位信息就浪费了。解决：把高 16 位异或到低 16 位：h ^ (h >>> 16)
 *   （>>> 是无符号右移，左边补 0）这就是 JDK HashMap 的 hash()。
 *
 * 【坑四：key 是 null 怎么办？】null.hashCode() 会空指针。
 *   约定：null 的 hash 固定为 0，永远放在 table[0]。
 *
 * 【验收红线（测试用例用反射在检查，字段名一个字母都不能改）】
 *   1. 成员变量必须叫 table（数组）
 *   2. 成员变量必须叫 size
 *   3. 内部类必须叫 Node，且要有 hash 字段、next 字段（类型就是 Node 自己）
 *   4. 必须有 public static int hash(Object key)，供测试直接调用
 *   5. 初始容量 16，负载因子 0.75，第 13 个元素触发扩容到 32
 */
public class MyHashMap<K, V> {

    // ==================== 一、常量与字段 ====================

    /** 默认初始容量，必须是 2 的幂。
     *  只有 2 的幂才满足 (n-1) 二进制全是 1，hash & (n-1) 才等价于 hash % n。*/
    private static final int DEFAULT_CAPACITY = 16;

    /** 负载因子：装到容量的 75% 就扩容。
     *  太大→冲突多链表长；太小→浪费内存频繁扩容。0.75 是折中。*/
    private static final float LOAD_FACTOR = 0.75f;

    /** 桶数组（核心！）。每格叫一个 bucket（桶），桶里挂一条 Node 链表。
     *  类型必须是 Node[]，因为反射要查 "table" 这个字段名。*/
    private Node[] table;

    /** 当前实际存了多少个键值对。注意 size != table.length！
     *  table.length 是抽屉个数（容量），size 是装了几个数据。*/
    private int size;

    /** 当前容量，单独记一份方便 capacity() 直接返回。*/
    private int capacity;


    // ==================== 二、内部类 Node（链表节点）====================

    /**
     * 链表节点，像火车车厢：
     *   [hash|key|value|next] -> [hash|key|value|next] -> ... -> null
     *
     * 为什么要存 hash？
     *   ① 找的时候先比 hash，不同就不用 equals() 了（省一次字符串比较）
     *   ② 扩容 rehash 还要用，重新算 hashCode() 是浪费
     *
     * ⚠️ 字段名必须是 hash 和 next，反射在查这两个名字！
     */
    private class Node {
        final int hash;   // 扰动后的哈希值，算好就不变
        final K key;      // key 也不改（JDK 也是 final）
        V value;          // value 可以改，put 覆盖时只改它
        Node next;        // 指向下一个节点，null 表示链表结束

        Node(int hash, K key, V value, Node next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }


    // ==================== 三、构造方法 ====================

    /** 无参构造：造一个空抽屉柜，16 个格子，一个数据都没装。*/
    public MyHashMap() {
        this.capacity = DEFAULT_CAPACITY;        // 16
        this.table = new MyHashMap.Node[DEFAULT_CAPACITY]; // 只 new 了数组，16 格全是 null
        this.size = 0;                           // 一个键值对都没有
    }


    // ==================== 四、静态工具方法（测试组 5 直接调它）====================

    /**
     * 扰动函数。公式：h ^ (h >>> 16)
     *
     * 拆开讲：假设 h 是 32 位
     *   h        = 01001101 00101101 11001010 00110101
     *   h >>> 16 = 00000000 00000000 01001101 00101101   ← 整串右推 16 格，左补 0
     *   异或结果 = 01001101 00101101 10000111 00011000   ← 相同得 0，不同得 1
     *
     * 效果：高 16 位的信息被"搅拌"进低 16 位。后面 & (capacity-1) 只用得到低几位，
     * 没有这一步搅拌，高位的变化就白费了，冲突率飙升。
     */
    public static int hash(Object key) {
        if (key == null) {
            return 0;              // 死规矩：null 的 hash 固定为 0，永远挂 table[0]
        }
        int h = key.hashCode();
        return h ^ (h >>> 16);     // 高低位异或，搅一搅
    }


    // ==================== 五、核心方法 put ====================

    /**
     * 放入键值对。key 已存在则覆盖旧值并返回旧值；新 key 返回 null。
     *
     * 流程：算 hash → 算下标 → 看桶空不空
     *       → 空：新节点当头
     *       → 不空：顺链表找 → 找到相同 key：覆盖 value 返回旧值
     *                    → 没找到：新节点尾插，size++
     */
    public V put(K key, V value) {
        // ---- 0. 扩容检查：必须在插入【之前】判断！
        // 16*0.75=12，装到第 13 个才扩容，所以用 >= 而不是 >。
        // 写成 > 就会第 13 个装完才发现超标，测试"装 12 个时容量不变"会挂。
        if (size >= (int) (capacity * LOAD_FACTOR)) {
            resize(capacity << 1);         // 左移 1 位 = 乘 2：16→32→64
        }

        // ---- 1. 算 hash（null 安全）
        int hash = hash(key);

        // ---- 2. 算下标。capacity=16 时 capacity-1=15=二进制 1111
        // hash & 1111 把高位全抹掉只留最低 4 位，结果必然在 0~15。
        int index = hash & (capacity - 1);

        // ---- 3. 看这个桶是不是空的
        Node head = table[index];

        if (head == null) {
            // 3.1 桶空，无冲突，直接 new 节点当链表第一个元素
            table[index] = new Node(hash, key, value, null);
            size++;
            return null;                   // 没有旧值
        }

        // ---- 4. 桶不空，有链表，顺着比对
        // 4.1 先看链表头（head 变量已拿到，不用进循环）
        // "同一个 key" = hash 相等 && key 相等（== 或 equals）
        if (head.hash == hash && eq(key, head.key)) {
            V oldValue = head.value;
            head.value = value;            // 只换 value，节点对象不用换
            return oldValue;
        }

        // 4.2 头不是，往后遍历
        Node prev = head;                  // prev 始终指向 cur 的前一个
        Node cur = head.next;              // 从第二个节点开始看
        while (cur != null) {
            if (cur.hash == hash && eq(key, cur.key)) {
                V oldValue = cur.value;
                cur.value = value;
                return oldValue;           // 覆盖，size 不变！（测试组 2/4 重点）
            }
            prev = cur;
            cur = cur.next;
        }
        //修改成尾插，链表最后的元素next指向新node
        prev.next = new Node(hash, key, value, null);
        size++;
        return null;
    }


    // ==================== 六、get / containsKey ====================

    /** 根据 key 取值，找不到返回 null。*/
    public V get(K key) {
        Node node = getNode(key);          // 抽成独立方法，三处复用
        return (node == null) ? null : node.value;
    }

    /** 判断是否包含某个 key。
     *  ⚠️ get(key)==null 不能代替 containsKey(key)，
     *     因为可能 key 存在但 value 本来就是 null。*/
    public boolean containsKey(K key) {
        return getNode(key) != null;
    }


    // ==================== 七、核心方法 remove ====================

    /**
     * 删除 key 对应的节点，返回旧 value；不存在返回 null。
     * 难点：单向链表删节点必须拿到"前驱节点"。
     * 三种情形：①桶为空 ②删的是链表头 ③删的是中间/尾部
     */
    public V remove(K key) {
        int hash = hash(key);
        int index = hash & (capacity - 1);
        Node head = table[index];

        if (head == null) {
            return null;                   // 情形①：桶都是空的，直接投降
        }

        // 情形②：要删的就是链表头
        if (head.hash == hash && eq(key, head.key)) {
            table[index] = head.next;      // 把头往后挪一位，原头节点无人引用→被 GC
            size--;
            return head.value;
        }

        // 情形③：要删的是第二个及以后的节点
        Node prev = head;
        Node cur = head.next;
        while (cur != null) {
            if (cur.hash == hash && eq(key, cur.key)) {
                prev.next = cur.next;      // 关键一行：跳过 cur，把它从链条上摘掉
                // 删之前: prev -> cur -> cur.next
                // 删之后:  prev -----------> cur.next
                size--;
                return cur.value;
            }
            prev = cur;
            cur = cur.next;
        }

        return null;                       // 情形④：遍历完没找到
    }


    // ==================== 八、扩容 resize（最容易写错的地方）====================

    /**
     * 扩容：数组长度变 newCap，旧节点全部重新散列搬过去。
     *
     * 【为什么不能原地扩？】Java 数组长度固定，只能 new 一个更大的再搬家。
     *
     * 【rehash 的核心数学结论（面试常考，背下来）】
     * 旧容量 n，新容量 2n。(2n-1) 只是比 (n-1) 多了一位高位的 1。
     * 所以新下标只有两种可能：
     *   · 那一位是 0 → 新下标 = 旧下标（留在原位）
     *   · 那一位是 1 → 新下标 = 旧下标 + n（平移到"原位+旧容量"）
     * 结论：每个桶的链表拆成两条即可，不用重新算 hash！判断条件就是 hash & oldCap。
     */
    private void resize(int newCap) {
        Node[] oldTable = this.table;      // 先留住旧数组引用，不然 new 完就找不到了
        int oldCap = this.capacity;

        this.table = new MyHashMap.Node[newCap];     // 建新数组
        this.capacity = newCap;

        for (int i = 0; i < oldCap; i++) {
            Node node = oldTable[i];
            if (node == null) continue;    // 空桶跳过
            oldTable[i] = null;            // 顺手置空，帮 GC（好习惯）

            Node loHead = null, loTail = null;   // low 组：留在原下标 i
            Node hiHead = null, hiTail = null;   // high 组：挪到 i + oldCap

            while (node != null) {
                if ((node.hash & oldCap) == 0) {       // 新增的那一位是 0
                    if (loTail == null) loHead = node; else loTail.next = node;
                    loTail = node;
                } else {                                 // 新增的那一位是 1
                    if (hiTail == null) hiHead = node; else hiTail.next = node;
                    hiTail = node;
                }
                node = node.next;          // ⚠️ 忘这一行就是死循环！
            }

            if (loTail != null) { loTail.next = null; this.table[i] = loHead; }
            if (hiTail != null) { hiTail.next = null; this.table[i + oldCap] = hiHead; }
            // 尾巴必须封死，否则还指着旧链上的脏数据
        }
    }


    // ==================== 九、辅助方法 ====================

    /** 抽取"根据 key 找节点"逻辑，get / containsKey / remove 都复用它。
     *  一处写对，三处受益 —— DRY 原则，减少 bug 的经典手法。*/
    private Node getNode(K key) {
        int hash = hash(key);
        int index = hash & (capacity - 1);
        Node node = table[index];
        while (node != null) {
            if (node.hash == hash && eq(key, node.key)) {
                return node;
            }
            node = node.next;
        }
        return null;
    }

    /** 安全的 equals，能处理 key 为 null（我们允许 put(null, v)）。
     *  为什么不能直接 a.equals(b)？a 可能是 null → NullPointerException。
     *  正确顺序：先 == 判同引用（含双双为 null）→ 再判 a!=null && a.equals(b)。*/
    private static boolean eq(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }


    // ==================== 十、对外 API（测试用例调这些）====================

    public int size()     { return size; }
    public int capacity() { return capacity; }
}