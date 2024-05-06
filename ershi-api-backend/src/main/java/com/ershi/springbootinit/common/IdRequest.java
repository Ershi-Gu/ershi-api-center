package com.ershi.springbootinit.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通过 ID 查询接口请求
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 * 
 */
@Data
public class IdRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}