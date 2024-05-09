package com.ershi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ershi.common.model.entity.UserToInterfaceInfo;
import com.ershi.springbootinit.model.dto.usertointerfaceinfo.UserToInterfaceInfoQueryRequest;
import com.ershi.springbootinit.model.vo.InterfaceInfoVO;
import com.ershi.springbootinit.model.vo.UserToInterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author qingtian_jun
* @description 针对表【user_to_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2024-05-02 10:36:20
*/
public interface UserToInterfaceInfoService extends IService<UserToInterfaceInfo> {

    // region CRUD

    /**
     * 参数校验
     * @param userToInterfaceInfo
     * @param add
     */
    void validUserToInterfaceInfo(UserToInterfaceInfo userToInterfaceInfo, boolean add);

     /**
     * 获取查询条件
     *
     * @param userToInterfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<UserToInterfaceInfo> getQueryWrapper(UserToInterfaceInfoQueryRequest userToInterfaceInfoQueryRequest);

    /**
     * 获取接口封装
     * @param userToInterfaceInfo
     * @param request
     * @return {@link InterfaceInfoVO}
     */
    UserToInterfaceInfoVO getUserToInterfaceInfoVO(UserToInterfaceInfo userToInterfaceInfo, HttpServletRequest request);

    /**
     * 分页获取接口封装
     * @param userToInterfaceInfoPage
     * @param request
     * @return {@link Page}<{@link InterfaceInfoVO}>
     */
    Page<UserToInterfaceInfoVO> getUserToInterfaceInfoVOPage(Page<UserToInterfaceInfo> userToInterfaceInfoPage, HttpServletRequest request);

    // endregion
}

