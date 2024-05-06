package com.ershi.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.springbootinit.common.ErrorCode;
import com.ershi.springbootinit.exception.BusinessException;
import com.ershi.springbootinit.exception.ThrowUtils;
import com.ershi.springbootinit.mapper.InterfaceInfoMapper;
import com.ershi.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.ershi.springbootinit.model.entity.InterfaceInfo;
import com.ershi.springbootinit.model.vo.InterfaceInfoVO;
import com.ershi.springbootinit.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ershi-Gu
 * @description 针对表【interface_info(`interface_info`)】的数据库操作Service实现
 * @createDate 2024-04-24 22:59:17
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description, url, method, requestHeader, responseHeader)
                    , ErrorCode.PARAMS_ERROR);
        }
        // todo 有参校验
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称过长");
        }
    }


    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceinfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceinfoQueryRequest == null) {
            return queryWrapper;
        }
        Long id = interfaceinfoQueryRequest.getId();
        String name = interfaceinfoQueryRequest.getName();
        String description = interfaceinfoQueryRequest.getDescription();
        String url = interfaceinfoQueryRequest.getUrl();
        String method = interfaceinfoQueryRequest.getMethod();
        String requestHeader = interfaceinfoQueryRequest.getRequestHeader();
        String responseHeader = interfaceinfoQueryRequest.getResponseHeader();
        Integer status = interfaceinfoQueryRequest.getStatus();
        Long userId = interfaceinfoQueryRequest.getUserId();
        int current = interfaceinfoQueryRequest.getCurrent();
        int pageSize = interfaceinfoQueryRequest.getPageSize();
        String sortField = interfaceinfoQueryRequest.getSortField();
        String sortOrder = interfaceinfoQueryRequest.getSortOrder();

        // todo 查询条件拼接
//        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);

        return queryWrapper;
    }

    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceinfo, HttpServletRequest request) {
        if (interfaceinfo == null) {
            return null;
        }
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceinfo,interfaceInfoVO);
        return interfaceInfoVO;
    }


    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceinfoPage, HttpServletRequest request) {
        List<InterfaceInfo> interfaceinfoList = interfaceinfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceinforVOPage = new Page<>(interfaceinfoPage.getCurrent(),
                interfaceinfoPage.getSize(), interfaceinfoPage.getTotal());
        if (CollUtil.isEmpty(interfaceinfoList)) {
            return interfaceinforVOPage;
        }
        // 填充信息
        List<InterfaceInfoVO> interfaceinfoVOList = interfaceinfoList.stream().map(InterfaceInfoVO::objToVo).collect(Collectors.toList());
        interfaceinforVOPage.setRecords(interfaceinfoVOList);
        return interfaceinforVOPage;
    }
}




