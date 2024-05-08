package com.ershi.springbootinit.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.common.model.entity.InterfaceInfo;
import com.ershi.common.model.entity.User;
import com.ershi.common.service.InnerInterfaceInfoService;
import com.ershi.springbootinit.common.ErrorCode;
import com.ershi.springbootinit.exception.BusinessException;
import com.ershi.springbootinit.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


@DubboService
public class InnerInterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InnerInterfaceInfoService {

    @Override
    public List<InterfaceInfo> getAllInterfaceInfo() {
        return this.list();
    }
}
