package com.ershi.springbootinit.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.ErrorCode;
import com.ershi.common.model.entity.InterfaceInfo;
import com.ershi.common.model.entity.User;
import com.ershi.common.model.entity.UserToInterfaceInfo;
import com.ershi.common.service.InnerUserToInterfaceInfoService;
import com.ershi.springbootinit.mapper.UserToInterfaceInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


@DubboService
public class InnerUserToInterfaceInfoServiceImpl extends ServiceImpl<UserToInterfaceInfoMapper, UserToInterfaceInfo>
        implements InnerUserToInterfaceInfoService {

    @Override
    public boolean invokeCount(long userId, long interfaceInfoId) {
        // 参数验证
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UpdateWrapper<UserToInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        // todo 验证是否还有查询次数（需要同步锁，数据库层面验证，非业务）
        updateWrapper.setSql("leftInvokeCount = leftInvokeCount - 1, invokeCount = invokeCount + 1");
        return this.update(updateWrapper);
    }

    @Override
    public boolean checkInvokeCount(User invokeUser, InterfaceInfo interfaceInfo) {
        Long userId = invokeUser.getId();
        Long interfaceInfoId = interfaceInfo.getId();

        QueryWrapper<UserToInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        UserToInterfaceInfo one = this.getOne(queryWrapper);

        if (one == null) {
            return false;
        }

        Integer leftInvokeCount = one.getLeftInvokeCount();
        if (leftInvokeCount <= 0) {
            return false;
        }

        return true;
    }


    @Override
    public boolean checkLog(UserToInterfaceInfo userToInterfaceInfo) {
        Long userId = userToInterfaceInfo.getUserId();
        Long interfaceInfoId = userToInterfaceInfo.getInterfaceInfoId();

        QueryWrapper<UserToInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        UserToInterfaceInfo one = this.getOne(queryWrapper);

        if (one == null) {
            return false;
        }

        return true;
    }
}
