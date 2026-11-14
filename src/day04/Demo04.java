package src.day04;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 你们后台的商品搜索词统计：每天 50 万条搜索日志，运营要看 Top 20 热词。日志的词表规模 5000。
 * 1. 用 HashMap 还是 ArrayList 存词频？用复杂度说明。
 *  hashMap  因为hashmap时间复杂度是O（1） arraylist是o(n)
 * 2. new HashMap<>(?) 该填多少？写出算式——注意别用 50 万算，先想清楚到底有多少个不同的 key。
 *  5000 / 0.75 = 6667 → 8192，key 只有 5000 个
 * 3. 如果这 50 万条日志是 4 个线程同时写进同一个 map，会出什么问题？换什么？
 * 线程安全问题 ConcurrentHashMap或者 Collections.synchronizedMap
 */
public class Demo04 {
    private static int pass = 0;
    private static int fail = 0;
    private static void check(String caseName, Object actual, Object expect) {
        boolean ok = (expect == null) ? (actual == null) : expect.equals(actual);
        if (ok) pass++; else fail++;
        System.out.println((ok ? "✅ " : "❌ ") + caseName
                + "    实际=" + actual + "  期望=" + expect);
    }

    public static void main(String[] args) {
        String[] vocab = buildVocab(5000);
        String[] log   = buildLog(vocab, 500_000);      // 50 万条日志
        String text    = String.join(" ", log);
        Map<String,Integer> freq = WordCount.count(text);
        int size = freq.size();
        List<String> strings = WordCount.topN(freq, 5);
        check("top5",strings.toString(),"[term_2341, term_4880, term_0016, term_2255, term_2299]");
        check("count size", size, 5000);
        System.out.println(strings.toString());
        int i1 = WordCount.capacityFor(5000);
        check("capacity for", i1, 8192);
        // 默认构造：16 起步，一路扩容上去
        long t1 = System.currentTimeMillis();
        Map<Integer,Integer> a = new HashMap<>();
        for (int i = 0; i < 1_000_000; i++) a.put(i, i);
        long costA = System.currentTimeMillis() - t1;

       // 预分配：一次给够，不扩容
        long t2 = System.currentTimeMillis();
        Map<Integer,Integer> b = new HashMap<>(2 * 1024 * 1024);
        for (int i = 0; i < 1_000_000; i++) b.put(i, i);
        long costB = System.currentTimeMillis() - t2;

        System.out.println("costA=" + costA + ", costB=" + costB);
        check("预分配不更慢", (costB <= costA)+"", "true");
        // ========== 汇总 ==========
        System.out.println();
        System.out.println("===== 通过 " + pass + " / 失败 " + fail + " =====");
        if (fail > 0) {
            System.out.println("还有 ❌，先修完再交卷。");
        } else {
            System.out.println("全绿，可以交卷。记得把这一行发给我。");
        }
    }
    private static String[] buildVocab(int size) {
        String[] v = new String[size];
        for (int i = 0; i < size; i++) v[i] = String.format("term_%04d", i + 1);
        return v;
    }

    private static String[] buildLog(String[] vocab, int n) {
        Random r = new Random(20261111L);          // 固定种子 = 可复现
        String[] log = new String[n];
        for (int i = 0; i < n; i++) {
            int k = 1 + r.nextInt(3);              // 每条 1~3 个词
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < k; j++) {
                if (j > 0) sb.append(' ');
                sb.append(vocab[r.nextInt(vocab.length)]);
            }
            log[i] = sb.toString();
        }
        return log;
    }
}
