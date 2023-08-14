package com.yushijie.leetcode.frequently.list;

import com.yushijie.leetcode.common.ListNode;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * 输入一个长度为 n 的链表，设链表中的元素的值为 ai ，返回该链表中倒数第k个节点。
 * 如果该链表长度小于k，请返回一个长度为 0 的链表。
 * @date 2023/6/16 11:06
 */
public class BM8 {

    public static void main(String[] args) {

    }

    /**
     * @author yushijie
     * @description 思路 快慢指针找到倒数第k个节点的前一个节点
     * @date 2023/6/16 11:19
     * @param pHead
     * @param k
     * @return com.yushijie.leetcode.common.ListNode
     */
    public static ListNode FindKthToTail (ListNode pHead, int k) {
        ListNode fast = pHead;
        ListNode slow = pHead;
        //快指针先行k步
        for(int i = 0; i < k; i++){
            if(fast != null)
                fast = fast.next;
                //达不到k步说明链表过短，没有倒数k
            else
                return slow = null;
        }
        //快慢指针同步，快指针先到底，慢指针指向倒数第k个
        while(fast != null){
            fast = fast.next;
            slow = slow.next;
        }
        return slow;
    }

}
