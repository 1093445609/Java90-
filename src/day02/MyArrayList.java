package src.day02;

import java.util.Arrays;
import java.util.Objects;

public class MyArrayList<E> {
    private Object[] elementData;   // 存数据
    private int size;               // 实际元素个数
    private static final int DEFAULT_CAPACITY = 10;
    // 无参构造：数组先给空的（学 ArrayList 的懒初始化；简化版可以直接给 10）
    public MyArrayList(){}
    // 指定初始容量
    public MyArrayList(int initialCapacity) {
        elementData = new Object[initialCapacity];
    }
    // 返回元素个数
    public int size()   {
        return size;
    }
    public boolean isEmpty(){
        return size == 0;
    }
    // 尾部追加；容量不够先扩容
    public void add(E e) {
        if (elementData == null) {
            elementData = new Object[DEFAULT_CAPACITY];
        }
        if (size == elementData.length) {
            grow(size + 1);
        }
        elementData[size] = e;
        size++;
    }
    // 越界抛 IndexOutOfBoundsException
    public E get(int index)  {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (E) elementData[index];
    }
    // 返回旧值
    public E set(int index, E e) {
       if (index < 0   || index >= size) {
           throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
       }
       E temp = (E) elementData[index];
       elementData[index] = e;
       return temp;
    }
    // 删除并返回被删元素；后面元素前移
    public E remove(int index)   {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        E temp = (E) elementData[index];
        //删除后 后面的元素往前移动

            System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);

        //最后的元素引用置为空 null: gc root 引用对象为空才能回收垃圾
        elementData[--size] = null;
        return temp;
    }
    // 删除第一个 equals 匹配的元素
    public boolean remove(Object o){
        if ( elementData == null){
            return false;
        }
        for (int i = 0; i < size; i++) {
                if (Objects.equals(elementData[i], o )) {
                    remove(i);
                    return true;
                }
            }

        return false;
    }
    // 扩容：1.5 倍，Arrays.copyOf
    private void grow(int minCapacity){
        int oldCapacity = elementData.length;
        //1.5倍扩容兼顾性能与容量内存不浪费综合考虑
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        //最小容量判断
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        //扩容新的长度
        elementData = Arrays.copyOf(elementData, newCapacity);
        System.out.println("触发扩容动作了=========》");
        System.out.println("newCapacity: " + newCapacity);
    }
    // 打印成 [a, b, c]
    @Override public String toString(){
        if (size == 0){
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append(elementData[i]);
            if (i != size - 1){
                sb.append(", ");
            }
        }
        sb.append("]");
       return  sb.toString();
    }
}
