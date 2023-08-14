package com.yushijie.leetcode.frequently.list;

import com.yushijie.leetcode.common.ListNode;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * 判断是否有环链
 * 单向链表如果无环一定是可以走到末尾的 即 next = null, 如果存在环链则一定无法走到末尾，这里采用双指针法，如果存在环链 那么快慢指针一定会碰撞
 * @date 2023/5/24 10:34
 */
public class BM6 {

    public static void main(String[] args) {

        ListNode head = new ListNode(0);
        ListNode first = new ListNode(1);
        ListNode second = new ListNode(2);
        ListNode third = new ListNode(3);
        head.next = first;
        first.next = second;
        second.next = third;
        third.next = first;

        System.out.println(hasCycle(head));
    }

    public static boolean hasCycle(ListNode head) {
        if (head == null) {
            return false;
        }
        //快指针每次走两步 慢指针每次走一步
        ListNode fast = head;
        ListNode slow = head;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;

            if (fast == slow) {
                return true;
            }
        }

        return false;

    }

}
