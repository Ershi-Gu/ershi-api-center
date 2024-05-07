package com.ershi.common.service;

/**
 * 内部处理用户请求接口相关 service
 * @author Ershi
 * @date 2024/05/06
 */
public interface InnerUserToInterfaceInfoService {

    /**
     * 统计调用次数(每次调用记录 + 1)
     * @param interfaceInfoId
     * @param userId
     * @return boolean
     */
    boolean invokeCount(long interfaceInfoId, long userId);

}
