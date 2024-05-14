package com.ershi.common.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

import java.util.Map;

public class HttpClientByAdmin {

    private String targetHost;
    private String targetUrl;

    public HttpClientByAdmin(String targetHost, String targetUrl) {
        this.targetHost = targetHost;
        this.targetUrl = targetUrl;
    }

    public String byGet(Map paramMap) {
        String result = HttpUtil.get(targetHost + targetUrl, paramMap);
        return result;
    }


    public String byPost(String paramJsonStr) {
        String result = HttpRequest.post(targetHost + targetUrl)
                .body(paramJsonStr)
                .execute().body();
        return result;
    }

    public static void main(String[] args) {
        HttpClientByAdmin httpClientByAdmin = new HttpClientByAdmin("http://api.ieus.cn", "/");
        String result = httpClientByAdmin.byGet(null);
        System.out.println(result);
    }
}
