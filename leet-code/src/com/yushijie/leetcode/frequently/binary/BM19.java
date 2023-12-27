package com.yushijie.leetcode.frequently.binary;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * 给定一个长度为n的数组nums，请你找到峰值并返回其索引。数组可能包含多个峰值，在这种情况下，返回任何一个所在位置即可。
 * 1.峰值元素是指其值严格大于左右相邻值的元素。严格大于即不能有等于
 * @date 2023/7/7 11:47
 */
public class BM19 {
    public static void main(String[] args) {

    }

    /**
     * @author yushijie
     * @description 思路：二分查找
     * @date 2023/7/7 15:00
     * @param nums
     * @return int
     */
    public static int findPeakElement (int[] nums) {
        int low = 0,mid = 0;
        int high = nums.length - 1;
        while (low < high) {
            mid = (low + high) / 2;
            if (nums[mid] > nums[mid + 1] && nums[mid] > nums[mid - 1]) {
                return mid;
            }else if (nums[mid] > nums[mid + 1]) {//比右边高 比左边低
                high = mid - 1;
            }else if (nums[mid] > nums[mid - 1]) {
                low = mid + 1;
            }
        }
        return mid;
    }
}
