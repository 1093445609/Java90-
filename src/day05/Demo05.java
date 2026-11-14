package src.day05;

/**
 * ============================================================
 *  Demo05：Day 5 业务用例（2 条）+ 业务 3 问
 * ============================================================
 *  骨架由导师预建（check / 汇总行已写好），你只做两件事：
 *    1. 在 main 里按 TODO 注释补 2 条用例的代码
 *    2. 在下面的【答】后面填业务 3 问
 *  运行 Demo05，交卷标准：===== 通过 2 / 失败 0 =====
 *
 * ------------------------------------------------------------
 * 业务 3 问（各写 2—3 行，不会写「不会」）：
 *
 * ① 管理后台删除接口，删除一个不存在的 id：应该抛异常，还是返回「未找到」的结果对象？说理由。
 * 【答】不应该抛异常删除一个不存在的id或者已经删除成功，但是手抖请求了两次，这些都是正常的不应该抛出异常，正常返回接口成功提示“已删除或者未找到”
 *
 * ② DAO 层抛出检查异常 SQLException，Controller 要给前端返回统一 JSON——中间应该怎么处理？
 * 【答】 try catch住异常后获取异常创建统一的业务异常 返回定义好的异常编码， 具体错误信息只打印日志
 *
 * ③ 日志系统每秒上千次拼 userId + ":" + action，用 String + 还是 StringBuilder？为什么？
 * 【答】 StringBuilder,因为+拼接会产生new StringBuilder对象append后toString,大量拼接会产生大量临时创建对象，占用内存
 * ------------------------------------------------------------
 */
public class Demo05 {

    private static int pass = 0;
    private static int fail = 0;

    private static void check(String caseName, Object actual, Object expect) {
        boolean ok = (expect == null) ? (actual == null) : expect.equals(actual);
        if (ok) pass++; else fail++;
        System.out.println((ok ? "✅ " : "❌ ") + caseName
                + "    实际=" + actual + "  期望=" + expect);
    }

    public static void main(String[] args) {

        // ========== 用例 ①：扣款失败后余额不变 ==========
        // 背景：balance=100，尝试 withdraw(100, 500)，余额不足必须抛 InsufficientBalanceException，
        //      catch 住之后断言：newBalance 仍然是 100。
        // 提示：withdraw 抛的是检查异常，你必须 try-catch（这个 catch 本身就是用例的一部分）。
        //
        // 写法参考（把注释打开补完，变量名自己起）：
        int balance = 100;
        int newBalance = balance;
        try {
            newBalance = ExceptionLab.withdraw(balance, 500);   // 这里应该抛
        } catch (InsufficientBalanceException e) {
            // 扣款失败，newBalance 不动
        }
        check("扣款失败后余额不变", newBalance, 100);
        //
        // TODO 在这里写用例 ①


        // ========== 用例 ②：StringBuilder 循环拼日志 ==========
        // 背景：拼出 "user0;user1;user2;"（循环 0..2，每项后跟分号），断言与字面量相等。
        // 要求：必须用 StringBuilder 循环拼，不许直接写字面量糊弄（同 StringPool 红线）。
        //
        // TODO 在这里写用例 ②
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append("user").append(i).append(";");
        }
        check("StringBuilder 拼日志", sb.toString(), "user0;user1;user2;");


        System.out.println("===== 通过 " + pass + " / 失败 " + fail + " =====");
        if (fail > 0) {
            System.out.println("还有 ❌，先修完再交卷。");
        } else if (pass == 0) {
            System.out.println("还是空壳——两条用例都还没写。写完再交。");
        } else {
            System.out.println("全绿，可以交卷。记得把这一行发给我。");
        }
    }
}
