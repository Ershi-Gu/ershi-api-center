package com.ershi.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 调用接口请求
 *
 * @author Eershi
 * @date 2024/04/30
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     *用户请求参数
     */
    private String userRequestParams;


    private static final long serialVersionUID = 1L;
}