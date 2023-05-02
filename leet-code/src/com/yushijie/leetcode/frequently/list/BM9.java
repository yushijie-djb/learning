package com.yushijie.leetcode.frequently.list;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * 给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。
 * 解决思路
 * 快慢指针 快指针先走n步(快指针不指向null)，然后快慢指针一起后移，快指针到末尾了，慢指针指向的就是待删除节点的前一个节点
 * @date 2023/3/6 10:41
 */
public class BM9 {

    public static void main(String[] args) {
        ListNode head = new ListNode(0);
    }

    public ListNode remove(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode first = head;
        ListNode second = dummy;
        for (int i = 0; i < n; ++i) {
            first = first.next;
        }
        while (first != null) {
            first = first.next;
            second = second.next;
        }
        second.next = second.next.next;
        ListNode ans = dummy.next;
        return ans;
    }

}
