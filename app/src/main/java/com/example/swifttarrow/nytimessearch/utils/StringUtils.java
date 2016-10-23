package com.example.swifttarrow.nytimessearch.utils;

/**
 * Created by swifttarrow on 10/22/2016.
 */

public class StringUtils {
    public static String surroundWithQuotes(String s){
        if (s == null) return null;
        StringBuilder sb = new StringBuilder("\"");
        sb.append(s).append("\"");
        return sb.toString();
    }
}
