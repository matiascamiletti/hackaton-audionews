package com.mobileia.audionews.util;

import static android.text.Html.escapeHtml;

/**
 * Created by matiascamiletti on 26/9/15.
 */
public class StringUtil {

    public static final int SIZE_PARRAFO = 20;

    public static int countWords(String s){
        return s.split(" ").length;
    }

    public static int countWordsOffset(String s, int start){
        int offset = 0;
        int words = 0;

        if(start >= s.length()){
            return s.length();
        }

        for (int i = 0; i < s.length(); i++) {
            if(Character.isSpaceChar(s.charAt(i))){
                words++;
            }
            if(start < offset){
                words = 0;
            }
            if(words == 20){
                break;
            }
            offset++;
        }

        System.out.println("Speech String offset: " + offset);

        return offset;
    }

    public static int countParrafos(String s){
        return 0;
    }

    /*public static int countWords(String s){

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;
        //Character.isSpaceChar()
        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }*/

    public static String trimString(String string, int length, boolean soft) {
        if(string == null || string.trim().isEmpty()){
            return string;
        }

        StringBuffer sb = new StringBuffer(string);
        int actualLength = length - 3;
        if(sb.length() > actualLength){
            // -3 because we add 3 dots at the end. Returned string length has to be length including the dots.
            if(!soft)
                return escapeHtml(sb.insert(actualLength, "").substring(0, actualLength+3));
            else {
                int endIndex = sb.indexOf(" ",actualLength);
                return escapeHtml(sb.insert(endIndex,"").substring(0, endIndex+3));
            }
        }
        return string;
    }

}
