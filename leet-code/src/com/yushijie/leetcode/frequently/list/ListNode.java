package com.yushijie.leetcode.frequently.list;

import java.util.Objects;

public class ListNode {
    int val;
    ListNode next = null;

    ListNode(int val) {
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
