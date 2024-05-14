package com.ershi.springbootinit.model.dto.usertointerfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建接口crud请求
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 * 
 */
@Data
public class UserToInterfaceInfoAddRequest implements Serializable {

    /**
     * 被调用接口 id
     */
    private Long interfaceInfoId;

    private static final long serialVersionUID = 1L;
}