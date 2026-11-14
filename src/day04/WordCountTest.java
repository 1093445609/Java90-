package src.day04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 *  Day 4 验收测试 —— 词频统计 + 容量核算
 * ============================================================
 *
 * 用法：你写 WordCount.java，跑到
 *      ===== 通过 53 / 失败 0 =====
 *
 * 这个文件不许改（导师记 md5）。改了断言让自己通过 = 作弊。
 *
 * 你要实现的四个方法（签名一个字都不能改）：
 *   public static Map<String,Integer> count(String text)
 *   public static List<String>        topN(Map<String,Integer> freq, int n)
 *   public static int                 capacityFor(int expectedSize)
 *   public static int[]               growthSequence(int n)
 *
 * ------------------------------------------------------------
 * 规格（规格之外的行为不考，规格之内必须精确）
 * ------------------------------------------------------------
 *
 * count(text)
 *   · text 为 null 或空串 → 返回空 Map（绝不能返回 null）
 *   · 切分规则：任何「非字母、非数字、非汉字、非下划线」的字符都是分隔符
 *     （逗号、句号、空格、制表符、换行、感叹号……）
 *   · 下划线算【词内字符】，不切分：sku_001 是一个词，不是 sku + 001
 *     （对应 Java 正则 \w 的定义，也对应你后台那种 SKU_20261108_0001 的命名）
 *   · 英文统一转小写（Hello 和 HELLO 算同一个词）
 *   · 汉字原样保留，连续汉字算一个词（苹果 是一个词）
 *   · 数字当词处理（2026 是一个词）
 *   · 连续分隔符不产生空词（"a,,,b" 只有 a 和 b）
 *
 * topN(freq, n)
 *   · freq 为 null / 空 → 返回空 List（绝不能返回 null）
 *   · n <= 0 → 返回空 List
 *   · n 超过 map 大小 → 返回全部，不许抛异常
 *   · 排序：先按次数【降序】；次数相同按 key 的 String.compareTo【升序】
 *   · 返回的是 key 的列表，不是 Entry
 *   · 不许修改传进来的 freq
 *
 * capacityFor(expectedSize)
 *   · 返回「能装下 expectedSize 条且一次都不扩容」的最小初始容量
 *   · 结果必须是 2 的幂；最小值 16
 *   · expectedSize <= 0 时返回 16
 *   · 依据：容量 × 0.75 >= expectedSize
 *
 * growthSequence(n)
 *   · 模拟 JDK HashMap 默认构造（容量 16、负载因子 0.75）装 n 个元素
 *   · 返回「每一次扩容之后的容量」组成的数组，按时间顺序
 *   · n <= 12 → 空数组（不是 null）
 *   · 例：n=13 → {32}；n=25 → {32,64}；n=100 → {32,64,128,256}
 */
public class WordCountTest {

    private static int pass = 0;
    private static int fail = 0;

    // ==================== 断言工具 ====================

    private static void check(String name, Object actual, Object expect) {
        boolean ok = (expect == null) ? (actual == null) : expect.equals(actual);
        if (ok) pass++; else fail++;
        System.out.println((ok ? "✅ " : "❌ ") + name
                + "    实际=" + str(actual) + "  期望=" + str(expect));
    }

    private static void checkArr(String name, int[] actual, int[] expect) {
        String a = (actual == null) ? "null" : Arrays.toString(actual);
        String e = (expect == null) ? "null" : Arrays.toString(expect);
        boolean ok = a.equals(e);
        if (ok) pass++; else fail++;
        System.out.println((ok ? "✅ " : "❌ ") + name + "    实际=" + a + "  期望=" + e);
    }

    private static String str(Object o) {
        if (o == null) return "null";
        if (o instanceof Object[]) return Arrays.toString((Object[]) o);
        return String.valueOf(o);
    }

    /** 捕获异常，用来断言"这里不该抛异常" */
    private static String err(Runnable r) {
        try { r.run(); return "没抛异常"; }
        catch (Throwable t) { return t.getClass().getSimpleName(); }
    }

    // ==================== 主流程 ====================

    public static void main(String[] args) {
        group1_count_basic();
        group2_count_split();
        group3_topN();
        group4_capacityFor();
        group5_growthSequence();
        group6_business();

        System.out.println();
        System.out.println("===== 通过 " + pass + " / 失败 " + fail + " =====");
        if (fail > 0) System.out.println("还有 ❌，先修完再交卷。");
    }

    // ---------- 组 1：count 基本 ----------
    private static void group1_count_basic() {
        System.out.println("--- 组 1：count 基本 ---");

        try {
            Map<String, Integer> m = WordCount.count(null);
            check("null 输入不返回 null", m != null, true);
            check("null 输入返回空表", (m == null) ? -1 : m.size(), 0);
        } catch (Throwable t) {
            check("null 输入不抛异常", "抛了 " + t.getClass().getSimpleName(), "没抛异常");
        }

        check("空串 → 空表", WordCount.count("").size(), 0);
        check("全是空格 → 空表", WordCount.count("     ").size(), 0);

        Map<String, Integer> one = WordCount.count("hello");
        check("单个词 size", one.size(), 1);
        check("单个词次数", one.get("hello"), 1);

        check("重复两次", WordCount.count("hello hello").get("hello"), 2);
        check("大小写归一 Hello/hello/HELLO", WordCount.count("Hello, hello! HELLO").get("hello"), 3);

        Map<String, Integer> ab = WordCount.count("apple banana apple");
        check("两词 size", ab.size(), 2);
        check("apple 次数", ab.get("apple"), 2);
        check("banana 次数", ab.get("banana"), 1);
    }

    // ---------- 组 2：count 切分规则 ----------
    private static void group2_count_split() {
        System.out.println("--- 组 2：count 切分 ---");

        check("逗号分号切分", WordCount.count("a,b;c").size(), 3);
        check("制表符换行切分", WordCount.count("a\tb\nc").size(), 3);
        check("连续分隔符不产生空词", WordCount.count("a,,,b").size(), 2);

        Map<String, Integer> cn = WordCount.count("苹果，苹果。香蕉");
        check("中文 size", cn.size(), 2);
        check("中文 苹果 次数", cn.get("苹果"), 2);
        check("中文 香蕉 次数", cn.get("香蕉"), 1);

        check("字母数字混合算一个词", WordCount.count("a1 b2 a1").get("a1"), 2);

        Map<String, Integer> ul = WordCount.count("sku_001 sku_001 sku_002");
        check("下划线不切分：size", ul.size(), 2);
        check("下划线不切分：sku_001 次数", ul.get("sku_001"), 2);

        Map<String, Integer> num = WordCount.count("2026 2026 年");
        check("纯数字当词", num.get("2026"), 2);
        check("数字与汉字分开", num.size(), 2);
    }

    // ---------- 组 3：topN ----------
    private static void group3_topN() {
        System.out.println("--- 组 3：topN ---");

        Map<String, Integer> m = new HashMap<>();
        m.put("a", 3);
        m.put("b", 2);
        m.put("c", 1);

        check("正常取前 2", WordCount.topN(m, 2), Arrays.asList("a", "b"));
        check("n 超过 size 返回全部", WordCount.topN(m, 99).size(), 3);
        check("n=0 返回空", WordCount.topN(m, 0).size(), 0);
        check("n 为负返回空", WordCount.topN(m, -5).size(), 0);
        check("空 map 返回空", WordCount.topN(new HashMap<>(), 3).size(), 0);

        try {
            check("null map 返回空", WordCount.topN(null, 3).size(), 0);
        } catch (Throwable t) {
            check("null map 不抛异常", "抛了 " + t.getClass().getSimpleName(), "没抛异常");
        }

        Map<String, Integer> tie = new HashMap<>();
        tie.put("b", 2);
        tie.put("a", 2);
        tie.put("c", 1);
        check("同次数按字典序升序", WordCount.topN(tie, 2), Arrays.asList("a", "b"));

        int before = m.size();
        WordCount.topN(m, 2);
        check("topN 不修改原 map", m.size(), before);
    }

    // ---------- 组 4：capacityFor ----------
    private static void group4_capacityFor() {
        System.out.println("--- 组 4：capacityFor ---");

        check("0 → 16", WordCount.capacityFor(0), 16);
        check("负数 → 16", WordCount.capacityFor(-100), 16);
        check("1 → 16", WordCount.capacityFor(1), 16);
        check("12 → 16（刚好装满阈值）", WordCount.capacityFor(12), 16);
        check("13 → 32（超阈值，翻倍）", WordCount.capacityFor(13), 32);
        check("98304 → 131072", WordCount.capacityFor(98304), 131072);
        check("98305 → 262144（131072 装不下）", WordCount.capacityFor(98305), 262144);
        check("100000 → 262144", WordCount.capacityFor(100000), 262144);
        check("结果必是 2 的幂（1000000 用例）", (WordCount.capacityFor(1000000) & (WordCount.capacityFor(1000000) - 1)), 0);
    }

    // ---------- 组 5：growthSequence ----------
    private static void group5_growthSequence() {
        System.out.println("--- 组 5：growthSequence ---");

        checkArr("n=0 → 空数组", WordCount.growthSequence(0), new int[]{});
        checkArr("n=12 → 不扩容", WordCount.growthSequence(12), new int[]{});
        checkArr("n=13 → 32", WordCount.growthSequence(13), new int[]{32});
        checkArr("n=25 → 32,64", WordCount.growthSequence(25), new int[]{32, 64});
        checkArr("n=100 → 32,64,128,256", WordCount.growthSequence(100), new int[]{32, 64, 128, 256});
        checkArr("n=1 → 空数组", WordCount.growthSequence(1), new int[]{});

        int[] big = WordCount.growthSequence(100000);
        check("10 万元素扩容次数", (big == null) ? -1 : big.length, 14);
        check("10 万元素最终容量", (big == null || big.length == 0) ? -1 : big[big.length - 1], 262144);
    }

    // ---------- 组 6：业务串起来 ----------
    private static void group6_business() {
        System.out.println("--- 组 6：业务串联 ---");

        String text = "java java spring spring spring boot";
        Map<String, Integer> freq = WordCount.count(text);
        check("业务：词表 size", freq.size(), 3);
        check("业务：Top2", WordCount.topN(freq, 2), Arrays.asList("spring", "java"));
        check("业务：Top1", WordCount.topN(freq, 1), Arrays.asList("spring"));

        check("count 与 topN 全量一致",
                WordCount.topN(freq, freq.size()).size(), freq.size());

        check("容量反推覆盖 10 万",
                WordCount.capacityFor(100000), 262144);
        check("扩容序列末值 == 容量反推值",
                WordCount.growthSequence(100000)[13], WordCount.capacityFor(100000));
    }
}
