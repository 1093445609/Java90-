package src.day05;

public class StringPool {
    //常量池比较
    public static Object literalEq() {
        String a = "a";
        String b = "a";
        return a == b;
    }
    //new 与常量池比较
    public static Object newStringEq() {
        String a = "a"; //产量池
        String b = new String("a"); //new 对象会分配对象到堆内存
        return a == b;
    }
    //intern() 返回池中同一对象
    public static Object internEq() {
        String a = "a";
        String b = new String("a");
        return a == b.intern();
    }
    //编译期常量折叠："ab"+"cd"=="abcd"
    public static Object foldEq() {

        String a = "ab" + "cd";
        String b = "abcd";
        return a == b;
    }
    //运行期拼接（含变量）== 字面量为 false
    public static Object runtimeConcatEq() {
        String a = "a";
        String b = a + "b";
        return b == "ab";
    }
    //运行期拼接 intern 后又 == 字面量
    public static Object runtimeConcatInternEq() {
        String a = "a";
        String b = a + "b";
        return b.intern() == "ab";
    }
    //toUpperCase/replace 不改原串
    public static Object immutableDemo() {
        String a = "Hello";
        a.toUpperCase();
        return a;
    }

    public static Object concatLoop(int i) {
        if ( i < 0 ){
            return "";
        }
        StringBuilder  a = new StringBuilder();
        for (int j = 0; j < i; j++) {
            a.append("a");
        }
        return a.toString();
    }
}
