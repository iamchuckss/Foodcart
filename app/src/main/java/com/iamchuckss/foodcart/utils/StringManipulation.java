package com.iamchuckss.foodcart.utils;

import java.util.List;

public class StringManipulation {

    public static String arrayToString(List<String> arr) {

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < arr.size(); i++) {
            sb.append(arr.get(i));
            if(i == arr.size() - 1) {
                break;
            }
            sb.append(", ");
        }

        return sb.toString();
    }
}
