package com.ershi.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数处理工具
 *
 * @author Ershi
 * @date 2024/05/07
 */
public class ParameterProcessor {

    /**
     * 处理 json 字符串的换行符号
     *
     * @param jsonString
     * @return {@link JsonNode}
     * @throws Exception
     */
    public static JsonNode parseJsonString(String jsonString) throws Exception {
        // 将换行符替换为空字符串
        jsonString = jsonString.replaceAll("\\\\n", "");

        // 解析JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        return jsonNode;
    }

    /**
     * 将 json 参数数据转换为 map
     *
     * @param jsonString
     * @return {@link Map}<{@link String}, {@link String}>
     * @throws Exception
     */
    public static Map<String, String> jsonToMap(String jsonString) throws Exception {

        if (jsonString == null) {
            return null;
        }

        // 创建ObjectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();

        // 将JSON字符串解析为JsonNode对象
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        // 创建Map对象来存储键值对
        Map<String, String> resultMap = new HashMap<>();

        // 遍历JsonNode对象，将每个参数添加到Map中
        jsonNode.fields().forEachRemaining(entry -> {
            String paramName = entry.getKey();
            String paramValue = entry.getValue().asText();
            resultMap.put(paramName, paramValue);
        });

        return resultMap;
    }
}
