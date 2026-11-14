package src.day03;

import java.util.Collections;

/**
 * 业务场景 3 问的答案：
 * ① 为什么 HashMap 不用 ArrayList 遍历
 * 答： ArrayList遍历时间复杂度是O（n）,hashmap是通过对key的hashcode 进行hash运算得出元素位置（n-1）&hash,时间复杂度是o（1）
 * ② new HashMap<>(131072) 的好处与这个数怎么来
 *  好处是指定了容量 省了扩容消耗，默认是16，131072个元素加入的话会扩容很多次，131072如果是2的幂则会在创建时指定容量131072 如果不是则会向上取数
 *  100000/0.75=133333→262144 （100001时大于容量133333*0.75 触发扩容2倍262144），  131072 只够 98304 98304就会扩容
 * ③ 多线程读写该换什么（2 个方案）
 * CurrentHashMap或者使用工具Collections.synchronizedMap()包装；
 *
 */
public class Demo03 {

    private static int pass = 0;
    private static int fail = 0;

    private static void check(String caseName, Object actual, Object expect) {
        boolean ok = (expect == null) ? (actual == null) : expect.equals(actual);
        if (ok) pass++; else fail++;
        System.out.println((ok ? "✅ " : "❌ ") + caseName
                + "    实际=" + actual + "  期望=" + expect);
    }

    public static void main(String[] args) {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("a", 1);
        check("put后get", m.get("a"), 1);
        check("get不存在key", m.get("b"), null);
        // ========== 汇总 ==========
        System.out.println();
        System.out.println("===== 通过 " + pass + " / 失败 " + fail + " =====");
        if (fail > 0) {
            System.out.println("还有 ❌，先修完再交卷。");
        } else {
            System.out.println("全绿，可以交卷。记得把这一行发给我。");
        }
    }
}
