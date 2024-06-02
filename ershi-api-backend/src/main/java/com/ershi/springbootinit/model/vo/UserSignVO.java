package com.ershi.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Ershi
 * @date 2024/06/02
 */
@Data
public class UserSignVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 签名：accessKey
     */
    private String accessKey;

    /**
     *签名：secreteKey
     */
    private String secreteKey;

    private static final long serialVersionUID = -1175552406588546330L;
}
