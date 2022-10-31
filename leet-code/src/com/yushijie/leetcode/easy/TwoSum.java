package com.yushijie.leetcode.easy;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author yushijie
 * @version 1.0
 * @description 两数之和
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
 * 你可以假设每种输入只会对应一个答案。
 * 输入：nums = [2,7,11,15], target = 9
 * 输出：[0,1]
 * 解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
 * @date 2022/10/17 10:22
 */
public class TwoSum {

    public static void main(String[] args) {
        int[] twoSum = twoSum(new int[]{3,3,4}, 6);
        Arrays.stream(twoSum).forEach(System.out::println);
    }

    public static int[] twoSum(int[] nums, int target) {
        //借助哈希表来降低时间复杂度(空间换时间) key数据值 value下标
        HashMap<Integer, Integer> integerHashMap = new HashMap<>();
        int[] result = new int[2];
        for (int i = 0; i < nums.length; i++) {
            if (integerHashMap.containsKey(target - nums[i])) {
                result[0] = integerHashMap.get(target - nums[i]);
                result[1] = i;
                return result;
            }
            integerHashMap.put(nums[i], i);
        }
        return result;
    }
}
