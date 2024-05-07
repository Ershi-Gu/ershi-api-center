package com.ershi.common.service;

import com.ershi.common.model.entity.User;

/**
 * 内部处理用户信息相关 service
 *
 * @author Ershi
 * @date 2024/05/06
 */
public interface InnerUserService {

    /**
     * 根据 accessKey 查询是否存在匹配用户
     * @param accessKey
     * @return {@link User}
     */
    User getInvokeUser(String accessKey);
}
