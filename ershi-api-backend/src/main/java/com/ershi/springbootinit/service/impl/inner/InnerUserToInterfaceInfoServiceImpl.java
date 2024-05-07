package com.ershi.springbootinit.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.common.model.entity.UserToInterfaceInfo;
import com.ershi.common.service.InnerUserToInterfaceInfoService;
import com.ershi.springbootinit.common.ErrorCode;
import com.ershi.springbootinit.exception.BusinessException;
import com.ershi.springbootinit.mapper.UserToInterfaceInfoMapper;
import com.ershi.springbootinit.service.UserToInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


@DubboService
public class InnerUserToInterfaceInfoServiceImpl extends ServiceImpl<UserToInterfaceInfoMapper, UserToInterfaceInfo>
        implements InnerUserToInterfaceInfoService {

    @Resource
    private UserToInterfaceInfoService userToInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userToInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }
}
