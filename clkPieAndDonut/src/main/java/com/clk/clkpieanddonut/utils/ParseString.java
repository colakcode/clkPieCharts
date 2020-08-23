package com.clk.clkpieanddonut.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ParseString {

    private static final String TAG = "clk_ParseString";

    public static List<String> get(String text, String sembol){
        List<String> list = new ArrayList<>();
        if(!text.equals(null)){
             list = new LinkedList<String>(Arrays.asList(text.split(sembol)));
        }
        return list;
    }

    public static String[] getWithBlank(String text){
        String[] splited = new String[0];
        if(!text.equals(null)){
             splited = text.split("\\s+");
            Log.d(TAG, "getWithBlank: ");
        }
        return splited;
    }

    public static String arrayToString(List<String> arrays, String sembol){
        String text="";
        for(int i=0; i<arrays.size();i++){
            if(i==0) text = arrays.get(i);
            else text = text + sembol + arrays.get(i);
        }
        return text;
    }
}
