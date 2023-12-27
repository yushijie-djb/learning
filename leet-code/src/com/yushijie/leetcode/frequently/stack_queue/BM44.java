package com.yushijie.leetcode.frequently.stack_queue;

import java.util.Stack;

/**
 * @author yushijie
 * @version 1.0
 * @description 给出一个仅包含字符'(',')','{','}','['和']',的字符串，判断给出的字符串是否是合法的括号序列
 * 括号必须以正确的顺序关闭，"()"和"()[]{}"都是合法的括号序列，但"(]"和"([)]"不合法。
 * @date 2023/7/18 10:14
 */
public class BM44 {

    public static void main(String[] args) {
        String s = "()[]{}(){}";
        System.out.println(isValid(s));
    }

    public static boolean isValid (String s) {

        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(')');
            } else if (s.charAt(i) == '[') {
                stack.push(']');
            } else if (s.charAt(i) == '{') {
                stack.push('}');
            } else if (stack.isEmpty() || stack.pop() != s.charAt(i)) {
                return false;
            }
        }
        return stack.isEmpty();
    }

}
