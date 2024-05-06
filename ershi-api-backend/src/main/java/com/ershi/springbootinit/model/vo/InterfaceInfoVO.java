package com.ershi.springbootinit.model.vo;

import com.ershi.springbootinit.model.entity.InterfaceInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 接口信息视图
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 *
 */
@Data
public class InterfaceInfoVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     *请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态 0 - 关闭  1- 开启
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param interfaceinfoVO
     * @return
     */
    public static InterfaceInfo voToObj(InterfaceInfoVO interfaceinfoVO) {
        if (interfaceinfoVO == null) {
            return null;
        }
        InterfaceInfo interfaceinfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceinfoVO, interfaceinfo);
        return interfaceinfo;
    }

    /**
     * 对象转包装类
     *
     * @param interfaceinfo
     * @return
     */
    public static InterfaceInfoVO objToVo(InterfaceInfo interfaceinfo) {
        if (interfaceinfo == null) {
            return null;
        }
        InterfaceInfoVO interfaceinfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceinfo, interfaceinfoVO);
        return interfaceinfoVO;
    }
}
