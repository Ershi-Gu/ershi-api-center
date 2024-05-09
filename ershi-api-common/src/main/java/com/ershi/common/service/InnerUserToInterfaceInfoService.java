package com.ershi.common.service;

import com.ershi.common.model.entity.InterfaceInfo;
import com.ershi.common.model.entity.User;

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

    /**
     * 检查该用户对该接口是否还有调用次数
     *
     * @param invokeUser 请求用户
     * @param interfaceInfo 请求接口
     * @return boolean 次数够用返回 true 否则返回 false
     */
    boolean checkInvokeCount(User invokeUser, InterfaceInfo interfaceInfo);

}
