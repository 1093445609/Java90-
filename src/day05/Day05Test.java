package src.day05;

/**
 * ============================================================
 *  Day 5 验收测试：异常体系 + String 常量池（23 条断言）
 * ============================================================
 * 运行方式：javac src/day05/*.java 后 java src.day05.Day05Test
 * 交卷标准：最后一行输出 ===== 通过 23 / 失败 0 =====
 *
 * 本文件由导师提供，不许修改（md5 校验，改断言 = 作弊）。
 * 你要写的三个文件：
 *   ExceptionLab.java            —— finally 陷阱 / 异常分类 / withdraw / try-with-resources
 *   InsufficientBalanceException —— 自定义检查异常
 *   StringPool.java              —— 常量池与 == 实验、不可变、拼接
 * 规格细节看 TASK.md 第 3 步。
 */
public class Day05Test {

    private static int pass = 0;
    private static int fail = 0;

    private static void check(String caseName, Object actual, Object expect) {
        boolean ok = (expect == null) ? (actual == null) : expect.equals(actual);
        if (ok) pass++; else fail++;
        System.out.println((ok ? "✅ " : "❌ ") + caseName
                + "    实际=" + actual + "  期望=" + expect);
    }

    interface Action { void run() throws Exception; }

    private static String exceptionOf(Action a) {
        try { a.run(); return "没抛异常"; }
        catch (Exception e) { return e.getClass().getSimpleName(); }
    }

    /** 包一层：把检查异常转成运行时抛出，方便直接断言返回值 */
    private static int safeWithdraw(int balance, int amount) {
        try { return ExceptionLab.withdraw(balance, amount); }
        catch (InsufficientBalanceException e) { throw new RuntimeException(e); }
    }

    public static void main(String[] args) {

        System.out.println("===== 组 1：finally 陷阱 =====");
        check("finally 的 return 覆盖 try 的 return", ExceptionLab.finallyWins(), "finally");
        check("finally 改局部变量救不回返回值", ExceptionLab.finallyCannotChange(), 1);

        System.out.println("\n===== 组 2：异常分类 =====");
        check("NullPointerException 是 unchecked", ExceptionLab.classify(new NullPointerException()), "unchecked");
        check("IOException 是 checked", ExceptionLab.classify(new java.io.IOException()), "checked");
        check("OutOfMemoryError 是 error", ExceptionLab.classify(new OutOfMemoryError()), "error");
        check("自定义异常继承 Exception 是 checked", ExceptionLab.classify(new InsufficientBalanceException("x")), "checked");
        check("普通 Exception 也是 checked", ExceptionLab.classify(new Exception()), "checked");

        System.out.println("\n===== 组 3：withdraw 自定义异常 =====");
        check("余额够：返回新余额", safeWithdraw(100, 30), 70);
        check("刚好扣到 0：合法", safeWithdraw(100, 100), 0);
        check("扣 0 元：合法", safeWithdraw(100, 0), 100);
        check("余额不足：抛 InsufficientBalanceException",
                exceptionOf(() -> ExceptionLab.withdraw(100, 200)), "InsufficientBalanceException");

        String msg = "没抛异常";
        try { ExceptionLab.withdraw(100, 200); }
        catch (InsufficientBalanceException e) { msg = e.getMessage(); }
        check("异常消息同时含余额(100)和所需(200)",
                msg.contains("100") && msg.contains("200"), true);

        check("金额为负：抛 IllegalArgumentException",
                exceptionOf(() -> ExceptionLab.withdraw(100, -5)), "IllegalArgumentException");

        System.out.println("\n===== 组 4：try-with-resources =====");
        check("打开顺序 a→b，body 先，关闭逆序 b→a",
                ExceptionLab.twrOrder(), "open:a;open:b;body;close:b;close:a;");

        System.out.println("\n===== 组 5：常量池与 == =====");
        check("字面量 == 字面量（同一池对象）", StringPool.literalEq(), true);
        check("new String 与字面量 == 为 false", StringPool.newStringEq(), false);
        check("intern() 返回池中同一对象", StringPool.internEq(), true);
        check("编译期常量折叠：\"ab\"+\"cd\"==\"abcd\"", StringPool.foldEq(), true);
        check("运行期拼接（含变量）== 字面量为 false", StringPool.runtimeConcatEq(), false);
        check("运行期拼接 intern 后又 == 字面量", StringPool.runtimeConcatInternEq(), true);

        System.out.println("\n===== 组 6：不可变与拼接 =====");
        check("toUpperCase/replace 不改原串", StringPool.immutableDemo(), "Hello");
        check("concatLoop(5) 拼出 5 个 a", StringPool.concatLoop(5), "aaaaa");
        check("concatLoop(0) 返回空串", StringPool.concatLoop(0), "");

        System.out.println();
        System.out.println("===== 通过 " + pass + " / 失败 " + fail + " =====");
        if (fail > 0) {
            System.out.println("还有 ❌，先修完再交卷。");
        } else {
            System.out.println("全绿，可以交卷。记得把这一行发给我。");
        }
    }
}
