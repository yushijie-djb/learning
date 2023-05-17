package com.yushijie.leetcode.frequently.list;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * 给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。
 * 解决思路
 * 快慢指针 快指针先走n步(快指针.next不指向null)，然后快慢指针一起后移，快指针到末尾了，慢指针指向的就是待删除节点的前一个节点
 * @date 2023/3/6 10:41
 */
public class BM9 {

    public static void main(String[] args) {
        ListNode head = new ListNode(0);
    }

    public static ListNode removeNthFromEnd(ListNode head, int n) {
        //定义虚拟头节点 防止对head的变更
        ListNode virtNode = new ListNode(-1);
        virtNode.next = head;
        ListNode fast = virtNode;
        ListNode slow = virtNode;

        //快指针先走N步
        for (int i = 0; i < n; i++) {
            fast = fast.next;
        }

        //fast.next != null 快慢指针一起移动
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }

        slow.next = slow.next.next;
        return virtNode.next;
    }

}
