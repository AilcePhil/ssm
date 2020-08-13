package com.powernode.utils;

import java.util.HashMap;
import java.util.Map;

public class GetCheckMessageUtil {



    public static Map<String, String> getCheckMessage(String message) {
        String messageNew = message.substring(1, message.length()-1);
        messageNew = messageNew.replace("\"", "");
        String[] split = messageNew.split(",");
        Map<String, String> map = new HashMap<>();
        for (String s1 : split) {
            String[] split1 = s1.split(":");
            for (int i= 0; i<split1.length; i+=2){
                map.put(split1[0],split1[1]);
            }
        }
        return map;

    }
}
