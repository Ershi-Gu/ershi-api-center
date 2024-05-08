package com.ershi.common.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

import java.util.Map;

/**
 * HTTP 请求工具
 *
 * @author Ershi
 * @date 2024/05/07
 */
public class HttpClientByAdmin {

    private String targetHost = "";
    private String targetUrl = "";

    public HttpClientByAdmin(String targetHost, String targetUrl) {
        this.targetHost = targetHost;
        this.targetUrl = targetUrl;
    }

    public String byGet(Map paramMap) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        String result = HttpUtil.get(targetHost + targetUrl, paramMap);
        return result;
    }


    public String byPost(String paramJsonStr) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        String result = HttpRequest.post(targetHost + targetUrl)
                .body(paramJsonStr)
                // 将参数和签名打包通过请求头传递
                .execute().body();
        return result;
    }
}
