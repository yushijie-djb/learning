package com.yushijie.leetcode.frequently.list;

import com.yushijie.leetcode.common.ListNode;

/**
 * @author yushijie
 * @version 1.0
 * @description 输入两个递增的链表，单个链表的长度为n，合并这两个链表并使新链表中的节点仍然是递增排序的。
 * 思路：双指针法
 * 如果l1指向的结点值小于等于l2指向的结点值，则将l1指向的结点值链接到cur的next指针，然后l1指向下一个结点值
 * 否则，让l2指向下一个结点值
 * 循环步骤1,2，直到l1或者l2为nullptr
 * 将l1或者l2剩下的部分链接到cur的后面
 * @date 2023/5/4 13:56
 */
public class BM4 {

    public static void main(String[] args) {
        ListNode node1 = new ListNode(1);
        ListNode node3 = new ListNode(3);
        ListNode node5 = new ListNode(5);
        node1.next = node3;
        node3.next = node5;

        ListNode node2 = new ListNode(2);
        ListNode node4 = new ListNode(4);
        ListNode node6 = new ListNode(6);
        node2.next = node4;
        node4.next = node6;

        ListNode merge = merge(node1, node2);
        System.out.println(merge);
    }

    public static ListNode merge(ListNode list1,ListNode list2) {
        //虚拟头节点
        ListNode virtual = new ListNode(-1);
        ListNode cur = virtual;

        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {//list1指针后移
                cur.next = list1;
                list1 = list1.next;
            } else {//list2指针后移
                cur.next = list2;
                list2 = list2.next;
            }
            cur = cur.next;//cur指针后移
        }

        //有可能list1为空退出 有可能list2为空退出 有可能list1，2都为空退出
        cur.next = list1 == null ? list1 : list2;
        return virtual.next;
    }

}
