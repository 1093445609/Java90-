package src.day01;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class demo01 {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Field field = ArrayList.class.getDeclaredField("elementData");
        field.setAccessible(true);
        for (int i = 0; i <= 100; i++) {
            list.add(i);
            Object[] inner = (Object[]) field.get(list);
            int size = list.size();
            if (size== 1 || size == 10 || size == 11 || size ==15 || size == 16 || size ==22){
                //jdk8 ArrayList
                // 1.初始化容量尚未分配  只有第一次add时会分配初始容量10
                // 2.当容量发生扩容时 最新容量 = 旧容量 + 旧容量的位运算位（除以2） 即1.5倍扩容
                // 3.size = 10 还没有扩容 当size = 11 发现不够分配才触发了扩容
                System.out.println("size:"+size+"数组真实长度=" + inner.length);
            }
        }

    }
}
