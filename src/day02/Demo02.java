package src.day02;

/**
 * Demo02 —— 断言式验证（这是标准写法，以后每天照抄这个结构）
 *
 * 核心思想：
 *   不要说"我跑了一遍没报错"，要说"这一条 实际 == 期望，✅"。
 *   有期望值，测试才有意义；没有期望值，那只是打印。
 */
public class Demo02 {

    // ======================= 一、断言小工具（复制到任何 Demo 都能用）=======================
    private static int pass = 0;
    private static int fail = 0;

    /**
     * @param caseName 用例名，写人话："remove(0) 删首元素"
     * @param actual   实际结果（真跑出来的）
     * @param expect   期望结果（你自己算出来的正确答案）
     */
    private static void check(String caseName, Object actual, Object expect) {
        boolean ok = (expect == null) ? (actual == null) : expect.equals(actual);
        if (ok) {
            pass++;
        } else {
            fail++;
        }
        System.out.println((ok ? "✅ " : "❌ ") + caseName
                + "    实际=" + actual + "  期望=" + expect);
    }

    // 反射读容量：private 字段外部拿不到，只能反射，用来验证扩容
    private static int capacity(MyArrayList<?> list) {
        try {
            java.lang.reflect.Field f = MyArrayList.class.getDeclaredField("elementData");
            f.setAccessible(true);
            Object[] arr = (Object[]) f.get(list);
            return arr == null ? 0 : arr.length;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 把"抛了什么异常"变成可比较的字符串；没抛就返回 "没抛异常"
    private static String exceptionOf(Runnable action) {
        try {
            action.run();
            return "没抛异常";
        } catch (Exception e) {
            return e.getClass().getSimpleName();
        }
    }

    // ======================= 二、用例子 =======================
    public static void main(String[] args) {

        // ---------- 组 1：基本增删改查 ----------
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        check("add 3 个后 size", list.size(), 3);
        check("toString 格式", list.toString(), "[a, b, c]");

        // set 返回"旧值"——最容易做错的约定
        check("set(1,\"B\") 返回旧值", list.set(1, "B"), "b");
        check("set 之后 get(1)", list.get(1), "B");

        // ---------- 组 2：remove(int) 三种位置 ----------
        MyArrayList<String> mid = new MyArrayList<>();
        mid.add("a"); mid.add("b"); mid.add("c");
        check("remove(1) 返回被删元素", mid.remove(1), "b");
        check("remove(1) 后内容", mid.toString(), "[a, c]");

        // 删第一个 —— 曾因为 if (index > 0) 导致整表错乱
        MyArrayList<String> first = new MyArrayList<>();
        first.add("a"); first.add("b"); first.add("c");
        first.remove(0);
        check("remove(0) 后内容", first.toString(), "[b, c]");
        check("remove(0) 后 size", first.size(), 2);

        // 删最后一个 —— 不需要搬移元素，最容易漏
        MyArrayList<String> last = new MyArrayList<>();
        last.add("a"); last.add("b"); last.add("c");
        last.remove(2);
        check("remove(2) 删尾后内容", last.toString(), "[a, b]");

        // ---------- 组 3：remove(Object) 和 null ----------
        MyArrayList<String> byObj = new MyArrayList<>();
        byObj.add("a"); byObj.add("b"); byObj.add("c");
        check("remove(\"b\") 命中", byObj.remove("b"), true);
        check("remove(\"b\") 后内容", byObj.toString(), "[a, c]");
        check("remove(\"zzz\") 未命中", byObj.remove("zzz"), false);

        // 关键用例：列表里有 null，删别的元素 —— 之前就是这里 NPE
        MyArrayList<String> hasNull = new MyArrayList<>();
        hasNull.add("x"); hasNull.add(null); hasNull.add("y");
        check("含null时 remove(\"y\")", hasNull.remove("y"), true);
        check("含null时 remove(\"y\") 后内容", hasNull.toString(), "[x, null]");

        // remove(null) 要真删掉那个 null 元素并返回 true
        MyArrayList<String> delNull = new MyArrayList<>();
        delNull.add("x"); delNull.add(null); delNull.add("y");
        check("remove(null) 返回", delNull.remove(null), true);
        check("remove(null) 后内容", delNull.toString(), "[x, y]");

        // ---------- 组 4：边界 ----------
        check("空表 toString", new MyArrayList<String>().toString(), "[]");
        check("空表 remove 不炸", new MyArrayList<String>().remove("a"), false);
        check("空表 isEmpty", new MyArrayList<String>().isEmpty(), true);

        MyArrayList<String> oob = new MyArrayList<>();
        oob.add("a");
        check("get(1) 抛异常类型", exceptionOf(() -> oob.get(1)), "IndexOutOfBoundsException");
        check("remove(-1) 抛异常类型", exceptionOf(() -> oob.remove(-1)), "IndexOutOfBoundsException");
        check("set(5) 抛异常类型", exceptionOf(() -> oob.set(5, "q")), "IndexOutOfBoundsException");

        // ---------- 组 5：扩容 ----------
        MyArrayList<String> grow = new MyArrayList<>();
        for (int i = 0; i < 12; i++) {
            grow.add(i + "");
        }
        // 10 装满 → 第 11 个扩容到 15；装到 12 个时容量仍是 15
        check("12 个元素后容量", capacity(grow), 15);
        check("12 个元素后 size", grow.size(), 12);
        check("扩容后 get(11)", grow.get(11), "11");

        // ---------- 组 6：删空全表，状态要回到初始 ----------
        MyArrayList<String> clear = new MyArrayList<>();
        for (int i = 0; i < 5; i++) {
            clear.add(i + "");
        }
        for (int i = 4; i >= 0; i--) {
            clear.remove(i);
        }
        check("删空后 size", clear.size(), 0);
        check("删空后 toString", clear.toString(), "[]");
        check("删空后 isEmpty", clear.isEmpty(), true);

        // ======================= 三、汇总 =======================
        System.out.println();
        System.out.println("===== 通过 " + pass + " / 失败 " + fail + " =====");
        if (fail > 0) {
            System.out.println("有失败用例，先修代码，别往下做。");
        }
    }
}
