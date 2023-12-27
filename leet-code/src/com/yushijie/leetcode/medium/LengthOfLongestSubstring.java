package com.yushijie.leetcode.medium;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yushijie
 * @version 1.0
 * @description 给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。
 * 示例 1:
 *
 * 输入: s = "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 * 示例 2:
 *
 * 输入: s = "bbbbb"
 * 输出: 1
 * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
 * 示例 3:
 *
 * 输入: s = "pwwkew"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
 *      请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
 * @date 2023/9/13 11:16
 */
public class LengthOfLongestSubstring {

    public static void main(String[] args) {
        int result = lengthOfLongestSubstring("acsedhacss");
        System.out.println(result);
    }


    //滑动窗口解决此类问题
    public static int lengthOfLongestSubstring(String s) {
        int right = 0;//窗口右边界
        int result = 0;//返回结果
        int length = s.length();
        Set<Character> characterSet = new HashSet<>();//由于题目要求只是返回长度，因此可以使用hashset

        for (int i = 0; i < length; i++) {//i就相当于窗口左边界
            if (i != 0) {//窗口左边界向前移动一位,对应的子串长度就减少一位 然后右窗口继续向右滑动
                characterSet.remove(s.charAt(i-1));
            }
            //窗口右边界向右滑动的过程中断条件
            //1.不能越界
            //2.集合中不能有重复字符
            while (right < length && !characterSet.contains(s.charAt(right))) {
                characterSet.add(s.charAt(right));
                ++right;
            }
            result = Math.max(result, characterSet.size());
        }
        return result;
    }

}
