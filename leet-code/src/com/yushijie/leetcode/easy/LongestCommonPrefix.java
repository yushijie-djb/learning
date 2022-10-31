package com.yushijie.leetcode.easy;

/**
 * @author yushijie
 * @version 1.0
 * @description 最长公共前缀 编写一个函数来查找字符串数组中的最长公共前缀。
 *
 * 如果不存在公共前缀，返回空字符串 ""。
 * 输入：strs = ["flower","flow","flight"]
 * 输出："fl"
 * @date 2022/10/31 10:23
 */
public class LongestCommonPrefix {

    /**
     * 思路: 首先这个String数组的遍历是少不了的，其次题目要求的是找所有字符串的公共前缀。
     * 因此我们可以两两比较得出一个公共前缀，然后再用这个公共前缀去跟后面的字符串比较找出公共前缀直到遍历完毕
     */
    public static void main(String[] args) {
        String[] sArr = {"flower", "flow", "flight", "yushijie"};
        String commonPrefix = sArr[0];
        for (int i = 1; i < sArr.length; i++) {
            commonPrefix = getCommonPrefix(commonPrefix, sArr[i]);
        }
        System.out.println(commonPrefix);
    }

    private static String getCommonPrefix(String s1, String s2) {
        int min = Math.min(s1.length(), s2.length());
        int index = 0;

        while (index < min && s1.charAt(index) == s2.charAt(index)) {
            index++;
        }
        return s1.substring(0, index);
    }

}
