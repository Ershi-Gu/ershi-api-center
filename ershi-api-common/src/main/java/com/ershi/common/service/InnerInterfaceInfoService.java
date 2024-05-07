package com.ershi.common.service;

import com.ershi.common.model.entity.InterfaceInfo;

import java.util.List;

/**
 * 内部处理接口相关信息 service
 * @author Ershi
 * @date 2024/05/06
 */
public interface InnerInterfaceInfoService {

    /**
     * 查询请求接口是否存在
     * @param path 请求路径
     * @param method 请求方法 POST / GET
     * @return {@link InterfaceInfo}
     */
    InterfaceInfo getInvokeInterfaceInfo(String url, String method);

    /**
     * 获取所有接口信息
     * @return {@link List}<{@link InterfaceInfo}>
     */
    List<InterfaceInfo>  getAllInterfaceInfo();
}
