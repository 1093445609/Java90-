package src.day01;

import java.util.ArrayList;
import java.util.List;

public class demo02 {

    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        for (String s : list) {
            if (s.equals("a")){
                //大概率抛 `ConcurrentModificationException`（也可能碰巧不抛——想想为什么）?
                //fail-fast 机制 ：遍历的时候维护了modCount
                //“fail-fast 是 Java 集合的一种错误检测机制。
                // ArrayList、HashMap 这类集合在创建迭代器时会记录当前的 modCount，之后每次调用 next() 都校验 modCount 是否被改变；
                // 如果在遍历过程中集合发生了结构性修改，就会立刻抛出 ConcurrentModificationException，快速失败而不是带着错误状态继续运行。
                // 它的设计目的是尽早暴露并发修改问题，但它不保证线程安全，也不能依赖它做正确性判断。
                // 单线程下避免的方式是用 Iterator.remove() 或 removeIf；
                // 多线程下要用 CopyOnWriteArrayList 或 ConcurrentHashMap 这类并发集合。”
                //CopyOnWriteArrayList 的原理一句话——写时复制：写操作加锁、复制一份新数组、改完替换引用；
                // 读操作完全无锁。代价是写慢、有内存开销，所以只适合读多写少（如黑名单、配置列表）
                list.remove(s);
            }
        }

    }
}
