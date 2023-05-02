package com.yushijie.leetcode.frequently.list;

import java.util.Objects;

/**
 * 将一个节点数为 size 链表 m 位置到 n 位置之间的区间反转
 * in: {1,2,3,4,5},2,4
 * out: {1,4,3,2,5}
 * 思路：
 * 找到指定区间的头节点和尾节点-切断前后关系-切断前需要保留节点信息
 * 翻转指定区间的链表
 * 重新拼接
 */
public class BM2 {
    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(3);
        ListNode node4 = new ListNode(4);
        head.next = node2;
        node2.next = node3;
        node3.next = node4;
        System.out.println("before reverse " + head);
        ListNode listNode = reverseBetween(head, 2, 3);
        System.out.println("after reverse " + listNode);
    }
    //0<m≤n≤size
    public static ListNode reverseBetween (ListNode head, int m, int n) {
        ListNode virtNode = new ListNode(-1);//使用虚拟头节点来避免头节点的复杂性
        virtNode.next = head;
        ListNode left = virtNode;

        for (int i = 0; i < m-1 ; i++) {
            System.out.println("i = " + i);
            left = left.next;
        }

        ListNode realRight = left;//避免重复遍历
        for (int i = 0; i < (n - m + 1); i++) {
            realRight = realRight.next;
        }

        ListNode realLeft = left.next;
        ListNode right = realRight.next;

        //切断
        left.next = null;
        realRight.next = null;

        ListNode listNode = BM1.reverseList(realLeft);
        left.next = listNode;//反转后的头节点就是之前left的next
        realLeft.next = right;//之前的realLeft已经变成了反转后的局部链表的尾节点
        return virtNode.next;
    }
}
