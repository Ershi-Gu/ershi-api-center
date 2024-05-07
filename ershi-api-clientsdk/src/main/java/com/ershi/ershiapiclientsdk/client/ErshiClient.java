package com.ershi.ershiapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ershi.ershiapiclientsdk.model.User;
import com.ershi.ershiapiclientsdk.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于请求第三方接口的HTTP客户端
 *
 * @author Eershi
 * @date 2024/04/27
 */
public class ErshiClient {

    private static final String GATEWAY_HOST = "http://localhost:9091";

    // 密钥
    private final String accessKey;

    private final String secretKey;

    // 使用客户端 SDK 时，需要传入自己的密钥，验证权限
    public ErshiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        return result;
    }


    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        return result;
    }


    public String getNameByPost(User user) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        String json = JSONUtil.toJsonStr(user);
        String result = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .body(json)
                // 将参数和签名打包通过请求头传递
                .addHeaders(getHeaderMap(json)).
                execute().body();
        return result;
    }


    /**
     * 获取请求头参数Map
     *
     * @param body 传递的参数
     * @return {@link Map}<{@link String}, {@link String}>
     */
    private Map<String, String> getHeaderMap(String body) {
        HashMap<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        map.put("body", body);
        // 随机数 nonce
        map.put("nonce", RandomUtil.randomNumbers(5));
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 通过请求头参数和密钥加密成签名
        map.put("sign", SignUtils.getSign(map, secretKey));
        return map;
    }

}
