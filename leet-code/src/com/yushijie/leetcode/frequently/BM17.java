package com.yushijie.leetcode.frequently;

/**
 * @author yushijie
 * @version 1.0
 * @description 二分查找
 * 给定一个 元素升序的、无重复数字的整型数组 nums 和一个目标值 target ，写一个函数搜索 nums 中的 target，如果目标值存在返回下标（下标从 0 开始），否则返回 -1
 * @date 2023/4/28 10:14
 */
public class BM17 {
    public static void main(String[] args) {
        int[] nums = {1,2,3,4,5,6,7,8,9};
        int search = search(nums, 1);
        System.out.println(search + "--" + nums[search]);
    }

    public static int search (int[] nums, int target) {
        // write code here
        int low = 0;
        int high = nums.length - 1;
        int mid = (low + high) / 2;
        while (low <= high) {
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                high = mid - 1;
                mid = (low + high) / 2;
            } else if (nums[mid] < target) {
                low = mid + 1;
                mid = (low + high) / 2;
            }
        }
        return -1;
    }
}
