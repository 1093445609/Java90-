package src.day01;

import java.util.*;

/**
 * 业务场景练习（15 分钟）
 * 管理后台「用户批量导入」：前端传 1 万条记录可能重复。①去重用什么集合？②要保持导入顺序怎么组合 List 和 Set？③每秒 100 次、每次 1 万条，要注意什么（开放题）。
 */
public class test01 {
    public static void main(String[] args) {
        //1模拟用户传入list
        List<String> list = new ArrayList<>(10000);
        for (int i = 0; i < 10000; i++) {
            list.add(String.valueOf(i));
        }
        //2 去重 如何保持有序
        //ai我的锅 我本意是准备一个空的set用于去重的 tab补全将list塞入了
        Set<String> set = new HashSet<>();
        //使用迭代器进行遍历 删除防止多线程 fail fast 快速失败机制
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            //通过set 去重
            if (!set.add(next)) {
                //通过list容器保持数据的有序性
                list.remove(next);
            }
        }
        //3.每秒 100 次、每次 1 万条，要注意什么（开放题）。
        // 答每秒一百次 需要注意限制频率为100 防止并发过大 内存占用大容易导致oom异常
        // 每次一万条需要注意分配容器的时候确认好list 初始容量而不是使用默认的10，这样会频繁的进行扩容，而开始就分配好一万容量就不会扩容占用内存


    }
}
