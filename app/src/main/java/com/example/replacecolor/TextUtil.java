package com.example.replacecolor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static String getColor(String text){
        Pattern pattern = Pattern.compile(">(.*)</color");
        Matcher matcher = pattern.matcher(text);
        String str = "";
        if (matcher.find()){
            str = matcher.group(1);
        }
        return str;
    }

    public static String getTag(String text){
        Pattern pattern = Pattern.compile("<color name=\"(.*)\"");
        Matcher matcher = pattern.matcher(text);
        String str = "";
        if (matcher.find()){
            str = matcher.group(1);
        }
        Pattern pattern1 = Pattern.compile("</color>(.*)");
        Matcher matcher1 = pattern1.matcher(text);
        String str1 = "";
        if (matcher1.find()){
            str1 = matcher1.group(1);
        }
        return str + str1;
    }
}
