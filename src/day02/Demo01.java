package src.day02;

import java.lang.reflect.Field;

public class Demo01 {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        MyArrayList list = new MyArrayList();
        System.out.println("size " + list.size());
        System.out.println("list is empty: " + list.isEmpty());
        for (int i = 0; i < 500000; i++) {
            list.add(i);
        }
        //默认对象的tostring 是调用了object的方法 返回hashcode地址之类的
        //所以需要重写toString方法
        System.out.println(list.toString());
        System.out.println("size " + list.size());
        /**
         * 你在做「操作日志批量写入」：日志对象 List 可能达到 50 万条。
         * 用你自己写的 MyArrayList 存，然后分析：
         * ①总扩容次数大约多少次？
         * ②总共复制了多少个元素？
         * ③如果一开始就 new MyArrayList(500000)，能省掉什么？
         * ④这种情况下你会真的用自己写的 MyArrayList 吗，还是用 ArrayList？（这道题考的是"能造"和"该用"的区分）
         *
         *  回答：
         *  1 数了下大概27次
         *  2 1,080,430 个，约等于最终容量的 2.2 倍。每次扩容都要把旧元素全量复制一遍，这是 1.5 倍扩容的真实代价
         *  3 可以省掉扩容次数和复制数组消耗  省掉 27 次扩容、108 万次元素复制、以及 27 个临时数组的 GC 压力
         *  4 用ArrayList 不要重复造轮子而且官方成熟的jdk会比自己写的好
         *  ArrayList 是工业级实现（迭代器、fail-fast、序列化、trimToSize、各类边界处理），自写版只适合学习。
         *  这就是"能造"和"该用"的区别
         */

    }
}
