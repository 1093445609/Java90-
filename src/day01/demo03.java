package src.day01;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class demo03 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        list.add("f");
        list.add("g");
        //1.正确的方式 通过迭代器移除元素
        Iterator <String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.equals("a")) {
                iterator.remove();
            }
        }
        //2.倒序迭代删除元素会发生什么？为什么？
        // 运行代码发现没有抛出异常了 不知道为啥
        for (int i = list.size() - 1; i > 0; i--) {
            String next = list.get(i);
            if (next.equals("b")) {
                list.remove(i);
            }
        }
    }
}
