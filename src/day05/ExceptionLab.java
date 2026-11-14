package src.day05;

public class ExceptionLab {
    //付款计算
    public static int withdraw(int balance, int amount) throws InsufficientBalanceException {
        if (balance < 0) {
            throw new InsufficientBalanceException("余额小于0");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("金额小于0");
        }
        if (balance < amount) {
            throw new InsufficientBalanceException("余额" + balance + " 不足，所需" + amount);
        }
        return balance - amount;
    }
    //finally
    public static Object finallyWins() {
        try {
            return "try";
        }catch (Exception e) {
            return "Exception";
        }finally {
            return "finally";
        }
    }

    public static Object finallyCannotChange() {
        int i = 1;
        try {
            //返回 i的值而不是引用对象
            return i;
        }catch (Exception e) {
            return "Exception";
        }finally {
            //不会改变返回值
             i = 2;
        }
    }

    public static Object classify(Throwable e) {
        if (e instanceof Error) {
           return  "error";
        } else if (e instanceof RuntimeException) {
            return  "unchecked";
        }else  {
            return  "checked";
        }
    }
    //try with catch 关闭资源顺序
    public static Object twrOrder() {
        // JDK7+：任何实现 AutoCloseable 的对象都能放进 try(...)
        StringBuilder sb = new StringBuilder();
        try (Res a = new Res("a", sb);      // 声明即注册
             Res b = new Res("b", sb)) {
            sb.append("body;");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
// 自动执行 a.close() b.close()，而且【后开的先关】（逆序）
// 输出：open:a; open:b; body; close:b; close:a;
        return sb.toString();
    }
}
