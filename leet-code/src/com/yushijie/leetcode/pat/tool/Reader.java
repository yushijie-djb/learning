package com.yushijie.leetcode.pat.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/7/31 11:17
 */
public class Reader {
    static BufferedReader reader;
    static StringTokenizer tokenizer;

    /** 调用此方法为 InputStream 初始化 reader */
    static void init(InputStream input) {
        reader = new BufferedReader(
                new InputStreamReader(input) );
        tokenizer = new StringTokenizer("");
    }

    /** 得到下一个词 */
    static String next() throws IOException {
        while ( ! tokenizer.hasMoreTokens() ) {
            //TODO 如有必要，添加对 eof 的检查
            tokenizer = new StringTokenizer(
                    reader.readLine() );
        }
        return tokenizer.nextToken();
    }

    static int nextInt() throws IOException {
        return Integer.parseInt( next() );
    }

    static double nextDouble() throws IOException {
        return Double.parseDouble( next() );
    }
}
