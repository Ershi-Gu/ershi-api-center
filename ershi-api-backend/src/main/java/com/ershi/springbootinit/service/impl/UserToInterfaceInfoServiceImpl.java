package com.ershi.springbootinit.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.ErrorCode;
import com.ershi.common.model.entity.UserToInterfaceInfo;
import com.ershi.springbootinit.mapper.UserToInterfaceInfoMapper;
import com.ershi.springbootinit.model.dto.usertointerfaceinfo.UserToInterfaceInfoQueryRequest;
import com.ershi.springbootinit.model.vo.UserToInterfaceInfoVO;
import com.ershi.springbootinit.service.UserToInterfaceInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ershi
 * @description 针对表【user_to_interface_info(用户调用接口关系表)】的数据库操作Service实现
 * @createDate 2024-05-02 10:36:20
 * @date 2024/05/03
 */
@Service
public class UserToInterfaceInfoServiceImpl extends ServiceImpl<UserToInterfaceInfoMapper, UserToInterfaceInfo>
        implements UserToInterfaceInfoService {

    @Override
    public void validUserToInterfaceInfo(UserToInterfaceInfo userToInterfaceInfo, boolean add) {
        if (userToInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = userToInterfaceInfo.getUserId();
        Long interfaceInfoId = userToInterfaceInfo.getInterfaceInfoId();
        Integer invokeCount = userToInterfaceInfo.getInvokeCount();
        Integer leftInvokeCount = userToInterfaceInfo.getLeftInvokeCount();
        Integer status = userToInterfaceInfo.getStatus();

        // 创建时，所有参数必须非空
        if (add) {
            if (userToInterfaceInfo.getInterfaceInfoId() <= 0 || userToInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userToInterfaceInfo.getLeftInvokeCount() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
        // todo 有参校验
    }


    @Override
    public QueryWrapper<UserToInterfaceInfo> getQueryWrapper(UserToInterfaceInfoQueryRequest userToInterfaceInfoQueryRequest) {

        QueryWrapper<UserToInterfaceInfo> queryWrapper = new QueryWrapper<>();

        if (userToInterfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        Long id = userToInterfaceInfoQueryRequest.getId();
        Long userId = userToInterfaceInfoQueryRequest.getUserId();
        Long interfaceInfoId = userToInterfaceInfoQueryRequest.getInterfaceInfoId();
        Integer invokeCount = userToInterfaceInfoQueryRequest.getInvokeCount();
        Integer leftInvokeCount = userToInterfaceInfoQueryRequest.getLeftInvokeCount();
        Integer status = userToInterfaceInfoQueryRequest.getStatus();
        int current = userToInterfaceInfoQueryRequest.getCurrent();
        int pageSize = userToInterfaceInfoQueryRequest.getPageSize();
        String sortField = userToInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userToInterfaceInfoQueryRequest.getSortOrder();


        // todo 查询条件拼接
//        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);

        return queryWrapper;
    }


    @Override
    public UserToInterfaceInfoVO getUserToInterfaceInfoVO(UserToInterfaceInfo userToInterfaceInfo, HttpServletRequest request) {
        if (userToInterfaceInfo == null) {
            return null;
        }
        UserToInterfaceInfoVO userToInterfaceInfoVO = new UserToInterfaceInfoVO();
        BeanUtils.copyProperties(userToInterfaceInfo, userToInterfaceInfo);
        return userToInterfaceInfoVO;
    }


    @Override
    public Page<UserToInterfaceInfoVO> getUserToInterfaceInfoVOPage(Page<UserToInterfaceInfo> userToInterfaceInfoPage, HttpServletRequest request) {
        List<UserToInterfaceInfo> userToInterfaceInfoList = userToInterfaceInfoPage.getRecords();
        Page<UserToInterfaceInfoVO> userToInterfaceInfoVOPage = new Page<>(userToInterfaceInfoPage.getCurrent(),
                userToInterfaceInfoPage.getSize(), userToInterfaceInfoPage.getTotal());
        if (CollUtil.isEmpty(userToInterfaceInfoList)) {
            return userToInterfaceInfoVOPage;
        }
        // 填充信息
        List<UserToInterfaceInfoVO> userToInterfaceInfoVOList = userToInterfaceInfoList.stream().map(UserToInterfaceInfoVO::objToVo).collect(Collectors.toList());
        userToInterfaceInfoVOPage.setRecords(userToInterfaceInfoVOList);
        return userToInterfaceInfoVOPage;
    }
}




