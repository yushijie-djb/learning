package com.yushijie.leetcode.frequently.list;

import com.yushijie.leetcode.common.ListNode;

/**
 * @author yushijie
 * @version 1.0
 * @description 反转单向链表 给定一个单链表的头结点pHead(该头节点是有值的，比如在下图，它的val是1)，长度为n，反转该链表后，返回新链表的表头。
 *
 * 数据范围：
 * 0
 * ≤
 * �
 * ≤
 * 1000
 * 0≤n≤1000
 * 要求：空间复杂度
 * �
 * (
 * 1
 * )
 * O(1) ，时间复杂度
 * �
 * (
 * �
 * )
 * O(n) 。
 * @date 2023/4/24 13:17
 */
public class BM1 {

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(3);
        ListNode node4 = new ListNode(4);
        head.next = node2;
        node2.next = node3;
        node3.next = node4;
        System.out.println("before reverse " + head);
        ListNode pre = reverseList(head);
        System.out.println(pre);
    }

    /**
     *  node1 -> node2 -> node3 -> node4
     *  cur      pre
     *  1.保存cur的next ListNode curNext = cur.next;
     *  2.交换cur 和 pre
     *  cur.next = pre;
     *  pre = cur;
     *  cur = curNext;
     */
    public static ListNode reverseList(ListNode head) {
        ListNode pre = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode curNext = cur.next;

            cur.next = pre;

            pre = cur;

            cur = curNext;
        }

        return pre;
    }

}

