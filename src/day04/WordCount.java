package src.day04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCount {

    /** 词：字母、数字、汉字、下划线组成的连续片段 */
    private static final Pattern WORD = Pattern.compile("[\\w\\u4e00-\\u9fff]+");

    public static Map<String, Integer> count(String text) {
        Map<String, Integer> freq = new HashMap<>();
        if (text == null || text.isEmpty()) {
            return freq;
        }

        Matcher m = WORD.matcher(text);
        while (m.find()) {
            String w = m.group();
            // 纯字母才统一小写；汉字、数字、下划线混合词原样保留
            if (w.codePoints().allMatch(Character::isLetter)) {
                w = w.toLowerCase();
            }
            freq.merge(w, 1, Integer::sum);
        }
        return freq;
    }
    //排序 + 截取
    public static List<String> topN(Map<String, Integer> freq, int n) {
        if (freq == null || freq.isEmpty() || n <= 0) {
            return Collections.emptyList();
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(freq.entrySet());
        list.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue()); // 次数降序
            if (cmp != 0) return cmp;
            return a.getKey().compareTo(b.getKey());               // key 字典序升序
        });

        int size = Math.min(n, list.size());
        List<String> ans = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ans.add(list.get(i).getKey());
        }
        return ans;
    }
    //除法 + 往上取 2 的幂
    public static int capacityFor(int expectedSize) {
        if (expectedSize <= 0) {
            return 16;
        }
        int cap = 16;
        while (cap * 0.75 < expectedSize) {
            cap <<= 1;
        }
        return cap;
    }
    //模拟扩容
    public static int[] growthSequence(int n) {
        if (n <= 12) {
            return new int[0];
        }
        List<Integer> seq = new ArrayList<>();
        int cap = 16;
        int threshold = (int) (cap * 0.75); // 12
        for (int i = 0; i < n; i++) {
            if (i == threshold) {
                cap <<= 1;
                threshold = (int) (cap * 0.75);
                seq.add(cap);
            }
        }
        int[] ans = new int[seq.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = seq.get(i);
        }
        return ans;
    }
}