package com.ershi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.ErrorCode;
import com.ershi.common.model.entity.InterfaceInfo;
import com.ershi.common.model.entity.User;
import com.ershi.common.response.BaseResponse;
import com.ershi.common.utils.*;
import com.ershi.ershiapiclientsdk.client.ErshiClient;
import com.ershi.springbootinit.annotation.AuthCheck;
import com.ershi.springbootinit.common.*;
import com.ershi.springbootinit.constant.UserConstant;
import com.ershi.springbootinit.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.ershi.springbootinit.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.ershi.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.ershi.springbootinit.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.ershi.springbootinit.model.enums.InterfaceStatusEnum;
import com.ershi.springbootinit.model.vo.InterfaceInfoVO;
import com.ershi.springbootinit.service.InterfaceInfoService;
import com.ershi.springbootinit.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * interface_info接口
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 */
@RestController
@RequestMapping("/interfaceinfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceinfoService;

    @Resource
    private UserService userService;


    // region 接口增删改查

    /**
     * 创建
     *
     * @param interfaceinfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceinfoAddRequest, HttpServletRequest request) {
        if (interfaceinfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceinfoAddRequest, interfaceinfo);

        // 参数校验
        interfaceinfoService.validInterfaceInfo(interfaceinfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceinfo.setUserId(loginUser.getId());
        boolean result = interfaceinfoService.save(interfaceinfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = interfaceinfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceinfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceinfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceinfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceinfoUpdateRequest) {
        if (interfaceinfoUpdateRequest == null || interfaceinfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceinfoUpdateRequest, interfaceinfo);

        // 参数校验
        interfaceinfoService.validInterfaceInfo(interfaceinfo, false);
        long id = interfaceinfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceinfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceinfoService.updateById(interfaceinfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfo = interfaceinfoService.getById(id);
        if (interfaceinfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceinfoService.getInterfaceInfoVO(interfaceinfo, request));
    }


    /**
     * 分页获取列表（仅管理员）
     *
     * @param interfaceinfoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceinfoQueryRequest) {
        long current = interfaceinfoQueryRequest.getCurrent();
        long size = interfaceinfoQueryRequest.getPageSize();
        Page<InterfaceInfo> interfaceinfoPage = interfaceinfoService.page(new Page<>(current, size),
                interfaceinfoService.getQueryWrapper(interfaceinfoQueryRequest));
        return ResultUtils.success(interfaceinfoPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceinfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceinfoQueryRequest,
                                                                         HttpServletRequest request) {
        long current = interfaceinfoQueryRequest.getCurrent();
        long size = interfaceinfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceinfoPage = interfaceinfoService.page(new Page<>(current, size),
                interfaceinfoService.getQueryWrapper(interfaceinfoQueryRequest));
        return ResultUtils.success(interfaceinfoService.getInterfaceInfoVOPage(interfaceinfoPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param interfaceinfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceinfoQueryRequest,
                                                                           HttpServletRequest request) {
        if (interfaceinfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        interfaceinfoQueryRequest.setUserId(loginUser.getId());
        long current = interfaceinfoQueryRequest.getCurrent();
        long size = interfaceinfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceinfoPage = interfaceinfoService.page(new Page<>(current, size),
                interfaceinfoService.getQueryWrapper(interfaceinfoQueryRequest));
        return ResultUtils.success(interfaceinfoService.getInterfaceInfoVOPage(interfaceinfoPage, request));
    }
    // endregion


    // region 接口上下线

    /**
     * 上线接口（仅管理员）
     *
     * @param idRequest 需要上线接口的 id
     * @param request
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        Long id = idRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询该接口信息是否存在
        InterfaceInfo interfaceInfo = interfaceinfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该接口不存在");
        }

        // 检验该接口是否可用(访问一下就知道是否可用了 - 通过 Http 客户端)
        String targetHost = interfaceInfo.getHost();
        String targetUrl = interfaceInfo.getUrl();
        if (StringUtils.isAnyBlank(targetHost, targetUrl)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口地址错误");
        }
        String requestParamsExample = interfaceInfo.getRequestParamsExample();
        String result = null;
        try {
            HttpClientByAdmin httpClientByAdmin = new HttpClientByAdmin(targetHost, targetUrl);
            if ("GET".equals(interfaceInfo.getMethod())) {
                result = httpClientByAdmin.byGet(ParameterProcessor.jsonToMap(requestParamsExample));
            } else if ("POST".equals(interfaceInfo.getMethod())) {
                result = httpClientByAdmin.byPost(requestParamsExample);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口无法访问");
        }

        // 修改 InterfaceInfo 的接口状态为 ONLINE
        InterfaceInfoUpdateRequest interfaceInfoUpdateRequest = new InterfaceInfoUpdateRequest();
        interfaceInfoUpdateRequest.setId(id);
        interfaceInfoUpdateRequest.setStatus(InterfaceStatusEnum.ONLINE.getValue());
        BaseResponse<Boolean> res = updateInterfaceInfo(interfaceInfoUpdateRequest);

        return res;
    }

    /**
     * 下线接口（仅管理员）
     *
     * @param idRequest 需要下线接口的 id
     * @param request
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/offline")

    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        Long id = idRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询该接口信息是否存在
        InterfaceInfo interfaceinfo = interfaceinfoService.getById(id);
        if (interfaceinfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该接口不存在");
        }

        // 修改 InterfaceInfo 的接口状态为 OFFLINE
        InterfaceInfoUpdateRequest interfaceInfoUpdateRequest = new InterfaceInfoUpdateRequest();
        interfaceInfoUpdateRequest.setId(id);
        interfaceInfoUpdateRequest.setStatus(InterfaceStatusEnum.OFFLINE.getValue());
        BaseResponse<Boolean> res = updateInterfaceInfo(interfaceInfoUpdateRequest);

        return res;
    }

    // endregion


    // region 接口调用

    /**
     * 调用在线接口
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/invoke")
    public BaseResponse<String> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {

        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }
        Long id = interfaceInfoInvokeRequest.getId();

        // 请求参数验证
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        if (StringUtils.isBlank(userRequestParams)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }

        // 检验该接口是否存在
        InterfaceInfo interfaceInfo = interfaceinfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }

        // 判断接口状态
        if (!Objects.equals(interfaceInfo.getStatus(), InterfaceStatusEnum.ONLINE.getValue())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口状态异常");
        }

        // 请求网关转发至对应接口
        String targetUrl = interfaceInfo.getUrl();
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secreteKey = loginUser.getSecreteKey();

        String result = null;
        try {
            HttpClient httpClient = new HttpClient(targetUrl, accessKey, secreteKey);
            if ("GET".equals(interfaceInfo.getMethod())) {
                result = httpClient.byGet(ParameterProcessor.jsonToMap(userRequestParams), id.toString());
            } else if ("POST".equals(interfaceInfo.getMethod())) {
                result = httpClient.byPost(userRequestParams, id.toString());
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口无法访问");
        }

        return ResultUtils.success(result);
    }

    // endregion

}
