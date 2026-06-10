package com.laserdentalrobot.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON解析工具 处理一些乱七八糟的json格式
 */
public class JsonCleanerUtil {

    public static JSONObject parseSmartJson(String rawJson) {
        if (StrUtil.isBlank(rawJson)) {
            return new JSONObject();
        }
        try {
            String cleaned = deepClean(rawJson);
            String fixed = fixBrokenJson(cleaned);
            return JSONUtil.parseObj(fixed);
        } catch (Exception e) {
            // 第一次解析失败 再试一次提取括号内容
            try {
                String bruteJson = extractJsonByBrackets(rawJson);
                return JSONUtil.parseObj(bruteJson);
            } catch (Exception ex) {
                return new JSONObject();
            }
        }
    }

    // 去掉markdown标记和注释
    
    private static String deepClean(String json) {
        if (StrUtil.isBlank(json)) {
            return "";
        }
        json = json.replaceAll("```json", "").replaceAll("```", "");
        json = json.replaceAll("~~~json", "").replaceAll("~~~", "");
        json = json.replaceAll("//.*", "");
        json = json.replaceAll("(?s)/\\*.*?\\*/", "");
        // 去掉AI常加的那些废话前缀
        json = json.replaceAll("(?i)(JSON结果|以下是JSON|返回结果|Result|Response)[:：]?\\s*", "");
        return json.trim();
    }

    // 修复常见json格式问题
    
    private static String fixBrokenJson(String json) {
        if (StrUtil.isBlank(json)) {
            return "";
        }
        json = json.replaceAll("(?<!\\\\)'", "\"");
        json = json.replaceAll(",\\s*}", "}");
        json = json.replaceAll(",\\s*]", "]");
        json = json.replaceAll("([{,])\\s*([a-zA-Z_$][a-zA-Z0-9_$]*)\\s*:", "$1\"$2\":");
        return json.trim();
    }

    // 暴力提取 找到第一个{}或[]
    private static String extractJsonByBrackets(String text) {
        if (StrUtil.isBlank(text)) {
            return "{}";
        }
        Matcher matcher = Pattern.compile("\\{[\\s\\S]*?\\}").matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        Matcher arrayMatcher = Pattern.compile("\\[[\\s\\S]*?\\]").matcher(text);
        if (arrayMatcher.find()) {
            return "{\"content\":" + arrayMatcher.group(0) + "}";
        }
        return "{}";
    }

    // 按路径取值 支持 a.b[0].c 这种写法
    
    public static String getValueByPath(JSONObject jsonObj, String path, String defaultValue) {
        if (jsonObj == null || StrUtil.isBlank(path)) {
            return defaultValue;
        }

        String[] pathNodes = path.split("\\.");
        Object current = jsonObj;

        for (String node : pathNodes) {
            if (current == null) {
                break;
            }
            if (current instanceof JSONObject) {
                JSONObject obj = (JSONObject) current;
                if (node.contains("[")) {
                    String[] arrNode = node.split("\\[");
                    String arrKey = arrNode[0];
                    int index = Integer.parseInt(arrNode[1].replace("]", ""));
                    JSONArray array = obj.getJSONArray(arrKey);
                    current = (array != null && index < array.size()) ? array.get(index) : null;
                } else {
                    current = obj.get(node);
                }
            } else if (current instanceof JSONArray) {
                try {
                    int index = Integer.parseInt(node);
                    current = ((JSONArray) current).get(index);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    current = null;
                }
            } else {
                current = null;
            }
        }

        return current != null ? current.toString() : defaultValue;
    }

    public static String extractValueFromRawJson(String rawJson, String path) {
        return extractValueFromRawJson(rawJson, path, "");
    }

    public static String extractValueFromRawJson(String rawJson, String path, String defaultValue) {
        JSONObject jsonObj = parseSmartJson(rawJson);
        return getValueByPath(jsonObj, path, defaultValue);
    }
}
