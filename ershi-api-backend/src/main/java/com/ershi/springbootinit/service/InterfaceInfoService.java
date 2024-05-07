package com.ershi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ershi.common.model.entity.InterfaceInfo;
import com.ershi.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.ershi.springbootinit.model.vo.InterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Ershi-Gu
* @description 针对表【interface_info(`interface_info`)】的数据库操作Service
* @createDate 2024-04-24 22:59:17
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 参数检验
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);


    /**
     * 获取查询条件
     *
     * @param interfaceinfoQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceinfoQueryRequest);


    /**
     * 获取接口封装
     * @param interfaceinfo
     * @param request
     * @return {@link InterfaceInfoVO}
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceinfo, HttpServletRequest request);


    /**
     * 分页获取接口封装
     * @param interfaceInfoPage
     * @param request
     * @return {@link Page}<{@link InterfaceInfoVO}>
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request);
}
