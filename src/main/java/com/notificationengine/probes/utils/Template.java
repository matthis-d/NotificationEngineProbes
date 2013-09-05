package com.notificationengine.probes.utils;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

public class Template {

    public static String evaluateTemplate(String template, HashMap<String, String> variables) {

        String result = template;

        for(String currentKey : variables.keySet()){

            String currentValue = variables.get(currentKey);

            String regex = "${"+currentKey+"}";

            result = StringUtils.replace(result, regex, currentValue);
        }

        return result;
    }
}
