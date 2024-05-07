package com.ershi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ershi.common.model.entity.User;
import com.ershi.common.model.entity.UserToInterfaceInfo;
import com.ershi.springbootinit.annotation.AuthCheck;
import com.ershi.springbootinit.common.BaseResponse;
import com.ershi.springbootinit.common.DeleteRequest;
import com.ershi.springbootinit.common.ErrorCode;
import com.ershi.springbootinit.common.ResultUtils;
import com.ershi.springbootinit.constant.UserConstant;
import com.ershi.springbootinit.exception.BusinessException;
import com.ershi.springbootinit.exception.ThrowUtils;
import com.ershi.springbootinit.model.dto.usertointerfaceinfo.UserToInterfaceInfoAddRequest;
import com.ershi.springbootinit.model.dto.usertointerfaceinfo.UserToInterfaceInfoQueryRequest;
import com.ershi.springbootinit.model.dto.usertointerfaceinfo.UserToInterfaceInfoUpdateRequest;
import com.ershi.springbootinit.model.vo.UserToInterfaceInfoVO;
import com.ershi.springbootinit.service.UserService;
import com.ershi.springbootinit.service.UserToInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * interface_info接口
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 */
@RestController
@RequestMapping("/usertointerfaceinfo")
@Slf4j
public class UserToInterfaceInfoController {

    @Resource
    private UserToInterfaceInfoService userToInterfaceInfoService;

    @Resource
    private UserService userService;


    // region 接口增删改查

    /**
     * 创建（仅管理员）
     *
     * @param userToInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserToInterfaceInfo(@RequestBody UserToInterfaceInfoAddRequest userToInterfaceInfoAddRequest,
                                                     HttpServletRequest request) {
        if (userToInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserToInterfaceInfo userToInterfaceInfo = new UserToInterfaceInfo();
        BeanUtils.copyProperties(userToInterfaceInfoAddRequest, userToInterfaceInfo);

        // 参数校验
        userToInterfaceInfoService.validUserToInterfaceInfo(userToInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userToInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userToInterfaceInfoService.save(userToInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newUserToInterfaceInfoId = userToInterfaceInfo.getId();
        return ResultUtils.success(newUserToInterfaceInfoId);
    }

    /**
     * 删除（仅管理员）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserToInterfaceInfo(@RequestBody DeleteRequest deleteRequest,
                                                           HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserToInterfaceInfo oldUserToInterfaceInfo = userToInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserToInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserToInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userToInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userToInterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserToInterfaceInfo(@RequestBody UserToInterfaceInfoUpdateRequest userToInterfaceInfoUpdateRequest) {
        if (userToInterfaceInfoUpdateRequest == null || userToInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserToInterfaceInfo userToInterfaceInfo = new UserToInterfaceInfo();
        BeanUtils.copyProperties(userToInterfaceInfoUpdateRequest, userToInterfaceInfo);

        // 参数校验
        userToInterfaceInfoService.validUserToInterfaceInfo(userToInterfaceInfo, false);
        long id = userToInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserToInterfaceInfo oldUserToInterfaceInfo = userToInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserToInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userToInterfaceInfoService.updateById(userToInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取（仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserToInterfaceInfoVO> getUserToInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserToInterfaceInfo userToInterfaceInfo = userToInterfaceInfoService.getById(id);
        if (userToInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userToInterfaceInfoService.getUserToInterfaceInfoVO(userToInterfaceInfo, request));
    }


    /**
     * 分页获取列表（仅管理员）
     *
     * @param userToInterfaceInfoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserToInterfaceInfo>> listUserToInterfaceInfoByPage(@RequestBody UserToInterfaceInfoQueryRequest userToInterfaceInfoQueryRequest) {
        long current = userToInterfaceInfoQueryRequest.getCurrent();
        long size = userToInterfaceInfoQueryRequest.getPageSize();
        Page<UserToInterfaceInfo> userToInterfaceInfoPage = userToInterfaceInfoService.page(new Page<>(current, size),
                userToInterfaceInfoService.getQueryWrapper(userToInterfaceInfoQueryRequest));
        return ResultUtils.success(userToInterfaceInfoPage);
    }

    /**
     * 分页获取列表（封装类）（仅管理员）
     *
     * @param userToInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserToInterfaceInfoVO>> listUserToInterfaceInfoVOByPage(@RequestBody UserToInterfaceInfoQueryRequest userToInterfaceInfoQueryRequest,
                                                                                     HttpServletRequest request) {
        long current = userToInterfaceInfoQueryRequest.getCurrent();
        long size = userToInterfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserToInterfaceInfo> userToInterfaceInfoPage = userToInterfaceInfoService.page(new Page<>(current, size),
                userToInterfaceInfoService.getQueryWrapper(userToInterfaceInfoQueryRequest));
        return ResultUtils.success(userToInterfaceInfoService.getUserToInterfaceInfoVOPage(userToInterfaceInfoPage, request));
    }

    // endregion
}
