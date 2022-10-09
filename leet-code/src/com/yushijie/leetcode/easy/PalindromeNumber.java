package com.yushijie.leetcode.easy;

/**
 * @author yushijie
 * @version 1.0
 * @description 回文数 给你一个整数(int) x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。
 *
 * 回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。
 *
 * 例如，121 是回文，而 123 不是。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode.cn/problems/palindrome-number
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @date 2022/10/9 18:10
 */
public class PalindromeNumber {

    public static void main(String[] args) {
        System.out.println(isPalindromeNumber(98589));
    }

    public static boolean isPalindromeNumber(int number) {
        //负数false
        if (number < 0) {
            return false;
        }
        //正数反转后比较是否相等
        int reverseNumber = 0;
        int tempNumber = number;
        while (tempNumber != 0) {
            reverseNumber = reverseNumber * 10 + tempNumber % 10;
            tempNumber = tempNumber / 10;
        }

        return reverseNumber == number;
    }
}
