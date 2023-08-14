package com.yushijie.leetcode.common;

import java.util.Objects;

public class ListNode {
    public int val;
    public ListNode next = null;

    public ListNode(int val) {
        this.val = val;
    }

    @Override
    public String toString() {
        String s = String.valueOf(val);
        if (Objects.nonNull(next)) {
            s = s + "-" + next.toString();
        }
        return s;
    }
}
