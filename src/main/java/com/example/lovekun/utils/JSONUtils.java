package com.example.lovekun.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizy
 * @date 2022/9/14 17:19
 */
public class JSONUtils {

    public static JSONObject keyToLowerCase(Object jsonObject) {
        if (!(jsonObject instanceof Map)) {
            return new JSONObject();
        }
        JSONObject res = new JSONObject();
        ((Map) jsonObject).entrySet().stream().forEach(entry -> {
            Object key = ((Map.Entry<?, ?>) entry).getKey();
            Object value = ((Map.Entry<?, ?>) entry).getValue();
            if (value instanceof List) {
                res.put(StrUtil.toCamelCase(key.toString().toLowerCase(Locale.ROOT)), keyToLowerCase((List) value));
            } else if (value instanceof Map) {
                res.put(StrUtil.toCamelCase(key.toString().toLowerCase(Locale.ROOT)), keyToLowerCase(value));
            } else {
                res.put(StrUtil.toCamelCase(key.toString().toLowerCase(Locale.ROOT)), value);
            }
        });
        return res;
    }

    public static List<JSONObject> keyToLowerCase(List<Object> jsonObjects) {
        return jsonObjects.stream().map(JSONUtils::keyToLowerCase).collect(Collectors.toList());
    }


}
