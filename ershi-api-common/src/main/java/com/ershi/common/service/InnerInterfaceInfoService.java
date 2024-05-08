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
     * 获取所有接口信息
     * @return {@link List}<{@link InterfaceInfo}>
     */
    List<InterfaceInfo>  getAllInterfaceInfo();
}
