package src.day03;

import java.lang.reflect.Field;

/**
 * Day 3 验收测试 —— 由导师提供，学员不要修改这个文件
 *
 * 用法：
 *   1. 先写一个"空壳" MyHashMap（所有方法 return null / 0），让本文件能编译
 *   2. 运行 main，会看到一片 ❌
 *   3. 一条一条实现，让 ❌ 变 ✅
 *   4. 全部 ✅ 之后交卷
 *
 * 红线：不许改这个文件里的任何断言来"让自己通过"。改了等于作弊，我会对比文件哈希。
 */
public class HashMapTest {

    private static int pass = 0;
    private static int fail = 0;

    private static void check(String caseName, Object actual, Object expect) {
        boolean ok = (expect == null) ? (actual == null) : expect.equals(actual);
        if (ok) pass++; else fail++;
        System.out.println((ok ? "✅ " : "❌ ") + caseName
                + "    实际=" + actual + "  期望=" + expect);
    }

    public static void main(String[] args) {

        // ========== 组 1：基本 put / get ==========
        MyHashMap<String, Integer> m = new MyHashMap<>();
        check("新建后 size", m.size(), 0);
        check("新建后容量", m.capacity(), 16);
        check("put 新 key 返回 null", m.put("a", 1), null);
        check("put 后 size", m.size(), 1);
        check("get 命中", m.get("a"), 1);
        check("containsKey 命中", m.containsKey("a"), true);
        check("containsKey 未命中", m.containsKey("zzz"), false);
        check("get 未命中返回 null", m.get("zzz"), null);

        // ========== 组 2：同 key 覆盖 ==========
        check("同 key put 返回旧值", m.put("a", 2), 1);
        check("覆盖后 size 不变", m.size(), 1);
        check("覆盖后 get 是新值", m.get("a"), 2);

        // ========== 组 3：null key（null 的 hash 固定为 0）==========
        MyHashMap<String, String> nul = new MyHashMap<>();
        check("put(null,...) 返回 null", nul.put(null, "n"), null);
        check("get(null)", nul.get(null), "n");
        check("containsKey(null)", nul.containsKey(null), true);
        check("null key 占 1 个 size", nul.size(), 1);
        check("remove(null) 返回旧值", nul.remove(null), "n");
        check("remove(null) 后 size", nul.size(), 0);
        check("remove(null) 后 get(null)", nul.get(null), null);

        // ========== 组 4：哈希冲突（"Aa" 和 "BB" 的 hashCode 都是 2112）==========
        check("\"Aa\".hashCode()", "Aa".hashCode(), 2112);
        check("\"BB\".hashCode()", "BB".hashCode(), 2112);

        MyHashMap<String, Integer> col = new MyHashMap<>();
        col.put("Aa", 1);
        col.put("BB", 2);
        check("冲突两 key 都在", col.size(), 2);
        check("冲突时 get(\"Aa\")", col.get("Aa"), 1);
        check("冲突时 get(\"BB\")", col.get("BB"), 2);
        col.put("Aa", 100);
        check("冲突链上覆盖 Aa", col.get("Aa"), 100);
        check("冲突链上覆盖后 BB 不变", col.get("BB"), 2);
        check("冲突链上覆盖后 size", col.size(), 2);
        check("remove 冲突链上的 Aa", col.remove("Aa"), 100);
        check("删 Aa 后 BB 还在", col.get("BB"), 2);

        // ========== 组 5：hash 扰动函数 ==========
        String[] keys = {"hello", "world", "Aa", "BB", "abc", "SKU_20261108_0001"};
        for (String k : keys) {
            int expected = k.hashCode() ^ (k.hashCode() >>> 16);
            check("扰动 hash(\"" + k + "\")", MyHashMap.hash(k), expected);
        }
        check("hash(null) 为 0", MyHashMap.hash(null), 0);

        // ========== 组 6：扩容（16 × 0.75 = 12，第 13 个触发）==========
        MyHashMap<Integer, Integer> grow = new MyHashMap<>();
        for (int i = 0; i < 12; i++) {
            grow.put(i, i * 10);
        }
        check("装 12 个时容量不变", grow.capacity(), 16);
        check("装 12 个时 size", grow.size(), 12);
        grow.put(12, 120);
        check("装第 13 个触发扩容到 32", grow.capacity(), 32);
        check("扩容后 size", grow.size(), 13);
        check("扩容后 get(0)", grow.get(0), 0);
        check("扩容后 get(11)", grow.get(11), 110);

        // ========== 组 7：remove ==========
        MyHashMap<String, Integer> rem = new MyHashMap<>();
        rem.put("a", 1); rem.put("b", 2); rem.put("c", 3);
        check("remove 返回旧值", rem.remove("b"), 2);
        check("remove 后 size", rem.size(), 2);
        check("remove 后 get", rem.get("b"), null);
        check("remove 未命中返回 null", rem.remove("zzz"), null);
        check("remove 未命中 size 不变", rem.size(), 2);
        check("还剩下的 a", rem.get("a"), 1);
        check("还剩下的 c", rem.get("c"), 3);

        // ========== 组 8：规模测试（触发多次扩容 + 长链表）==========
        MyHashMap<String, Integer> big = new MyHashMap<>();
        for (int i = 0; i < 1000; i++) {
            big.put("k" + i, i);
        }
        check("1000 个元素 size", big.size(), 1000);
        check("1000 个元素 get(k0)", big.get("k0"), 0);
        check("1000 个元素 get(k500)", big.get("k500"), 500);
        check("1000 个元素 get(k999)", big.get("k999"), 999);
        check("1000 个元素查询未命中", big.get("nope"), null);

        // 覆盖 1000 个里的一半，size 不该变
        for (int i = 0; i < 500; i++) {
            big.put("k" + i, i + 10000);
        }
        check("覆盖 500 个后 size 不变", big.size(), 1000);
        check("覆盖后新值生效", big.get("k0"), 10000);

        // 删一半
        for (int i = 0; i < 500; i++) {
            big.remove("k" + i);
        }
        check("删 500 个后 size", big.size(), 500);
        check("被删的查不到", big.get("k0"), null);
        check("没删的还在", big.get("k500"), 500);

        // ========== 组 9：删空 ==========
        MyHashMap<String, Integer> all = new MyHashMap<>();
        for (int i = 0; i < 10; i++) {
            all.put("x" + i, i);
        }
        for (int i = 0; i < 10; i++) {
            all.remove("x" + i);
        }
        check("删空后 size", all.size(), 0);
        check("删空后 get 全 null", all.get("x0"), null);
        check("删空后还能继续 put", all.put("again", 1), null);
        check("再 put 后 size", all.size(), 1);

        // ========== 组 10：结构自检（用反射检查内部是 数组 + 链表）==========
        check("内部有 table 字段", hasField("table"), true);
        check("内部有 size 字段", hasField("size"), true);
        check("Node 有 next 字段（链表）", nodeHasNext(), true);
        check("Node 有 hash 字段", nodeHasHash(), true);

        // ========== 汇总 ==========
        System.out.println();
        System.out.println("===== 通过 " + pass + " / 失败 " + fail + " =====");
        if (fail > 0) {
            System.out.println("还有 ❌，先修完再交卷。");
        } else {
            System.out.println("全绿，可以交卷。记得把这一行发给我。");
        }
    }

    // ---------- 反射自检工具 ----------
    private static boolean hasField(String name) {
        try {
            MyHashMap.class.getDeclaredField(name);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private static Class<?> nodeClass() {
        for (Class<?> c : MyHashMap.class.getDeclaredClasses()) {
            if (c.getSimpleName().contains("Node")) return c;
        }
        return null;
    }

    private static boolean nodeHasNext() {
        Class<?> n = nodeClass();
        if (n == null) return false;
        try {
            Field f = n.getDeclaredField("next");
            return f.getType() == n;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private static boolean nodeHasHash() {
        Class<?> n = nodeClass();
        if (n == null) return false;
        try {
            n.getDeclaredField("hash");
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
