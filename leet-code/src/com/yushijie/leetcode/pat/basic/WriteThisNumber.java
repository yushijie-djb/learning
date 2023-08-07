package com.yushijie.leetcode.pat.basic;

import java.util.Stack;

/**
 * @author yushijie
 * @version 1.0
 * @description 读入一个正整数 n，计算其各位数字之和，用汉语拼音写出和的每一位数字。
 * 输入格式：
 * 每个测试输入包含 1 个测试用例，即给出自然数 n 的值。这里保证 n 小于 10
 * 100
 *  。
 * 输出格式：
 * 在一行内输出 n 的各位数字之和的每一位，拼音数字间有 1 空格，但一行中最后一个拼音数字后没有空格。
 *
 * 输入样例：
 * 1234567890987654321123456789
 * 输出样例：
 * yi san wu
 * @date 2023/7/31 11:25
 */
public class WriteThisNumber {

    public static void main(String[] args) {
        write("0");
    }

    public static void write(String numLine) {
        char[] chars = numLine.toCharArray();
        int sum = 0;
        for (int i = 0; i < chars.length; i++) {
            sum = Character.getNumericValue(chars[i]) + sum;
        }
        Stack<Integer> stack = new Stack<>();
        if (sum == 0) {
            stack.push(0);
        }
        while (sum > 0) {
            stack.push(sum % 10);//放的是pinyin数组下标
            sum = sum / 10;
        }
        String[] pinyin = {"ling", "yi", "er", "san", "si", "wu", "liu", "qi", "ba", "jiu"};
        String result = "";
        while (!stack.isEmpty()) {
            Integer pop = stack.pop();
            result = result + pinyin[pop] + " ";
        }
        System.out.println(result.substring(0, result.length() - 1));
    }
}
