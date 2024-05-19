package com.ershi.common.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 请求工具
 *
 * @author Ershi
 * @date 2024/05/07
 */
public class HttpClient {

    // todo 配置网关地址
    private static final String GATEWAY_HOST = "http://localhost:9091";

    private String targetUrl = "";

    // 密钥
    private final String accessKey;

    private final String secretKey;


    public HttpClient(String targetUrl, String accessKey, String secretKey) {
        this.targetUrl = targetUrl;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String byGet(Map<String, String> paramMap, String interfaceInfoId) {

        String paramStr = HttpUtil.toParams(paramMap);
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        String result = HttpRequest.get(GATEWAY_HOST + targetUrl)
                .body(paramStr)
                .addHeaders(getHeaderMap(paramStr, interfaceInfoId))
                .execute()
                .body();
        return result;
    }


    public String byPost(String paramJsonStr, String interfaceInfoId) {

        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        String result = HttpRequest.post(GATEWAY_HOST + targetUrl)
                .body(paramJsonStr)
                .addHeaders(getHeaderMap(paramJsonStr, interfaceInfoId))
                // 将参数和签名打包通过请求头传递
                .execute()
                .body();
        return result;
    }


    private Map<String, String> getHeaderMap(String body, String interfaceInfoId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        map.put("body", body);
        // 随机数 nonce
        map.put("nonce", RandomUtil.randomNumbers(5));
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 通过请求头参数和密钥加密成签名
        map.put("sign", SignUtils.getSign(map, secretKey));
        map.put("id", interfaceInfoId);
        return map;
    }

}
