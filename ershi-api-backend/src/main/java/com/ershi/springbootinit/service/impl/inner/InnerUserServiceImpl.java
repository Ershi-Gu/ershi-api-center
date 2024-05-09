package com.ershi.springbootinit.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.ErrorCode;
import com.ershi.common.model.entity.User;
import com.ershi.common.service.InnerUserService;
import com.ershi.springbootinit.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;


@DubboService
public class InnerUserServiceImpl extends ServiceImpl<UserMapper, User>
        implements InnerUserService {

    @Override
    public User getInvokeUser(String accessKey) {

        if (StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return this.getOne(queryWrapper);
    }
}
