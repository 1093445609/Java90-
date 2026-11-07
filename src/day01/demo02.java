package src.day01;

import java.util.ArrayList;
import java.util.List;

public class demo02 {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        for (String s : list) {
            if (s.equals("a")){
                //大概率抛 `ConcurrentModificationException`（也可能碰巧不抛——想想为什么）?
                //
                list.remove(s);
            }
        }
    }
}
