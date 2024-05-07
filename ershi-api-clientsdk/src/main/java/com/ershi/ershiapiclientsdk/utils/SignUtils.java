package com.ershi.ershiapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import java.util.Map;

/**
 * 制作签名工具类
 * @author Eershi
 * @date 2024/04/27
 */
public class SignUtils {

    /**
     * 通过请求头参数和密钥制作签名
     * @param headerMap 请求头参数
     * @param secretKey 密钥
     * @return {@link String} 签名
     */
    public static String getSign(Map<String,String> headerMap, String secretKey){
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        String content = headerMap.toString() + "-" + secretKey;
        return digester.digestHex(content);
    }
}
