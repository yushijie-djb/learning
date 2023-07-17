package com.yushijie.leetcode.frequently.stack_queue;

import java.util.Stack;

/**
 * @author yushijie
 * @version 1.0
 * @description 用两个栈来实现一个队列，使用n个元素来完成 n 次在队列尾部插入整数(push)和n次在队列头部删除整数(pop)的功能。 队列中的元素为int类型。保证操作合法，即保证pop操作时队列内已有元素。
 * @date 2023/7/17 18:01
 */
public class BM42 {

    Stack<Integer> stack1 = new Stack<Integer>();
    Stack<Integer> stack2 = new Stack<Integer>();


    public void push(int node) {
        stack1.push(node);
    }

    //难点在于出栈 先把1号栈的内容全部添加到2号栈中，然后把2号栈出栈一个元素，再把剩余的元素出栈添加到1号栈
    public int pop() {
        //先把1号栈的内容全部添加到2号栈中
        while (!stack1.isEmpty()) {
            stack2.push(stack1.pop());
        }
        //然后把2号栈出栈一个元素
        Integer pop = stack2.pop();
        //再把剩余的元素出栈添加到1号栈
        while (!stack2.isEmpty()) {
            stack1.push(stack2.pop());
        }
        return pop;
    }

}
