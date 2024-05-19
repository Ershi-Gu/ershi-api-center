package com.ershi.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 * 
 */
@TableName(value = "user")
@Data
public class User implements Serializable {

    private static final String DEFAULT_USER_NAME = "man";
    private static final String DEFAULT_USER_Avatar = "https://cdn.xxhzm.cn/images/head/1743565738.jpg";

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;

    /**
     * 用户昵称
     */
    private String userName = DEFAULT_USER_NAME;

    /**
     * 用户头像
     */
    private String userAvatar = DEFAULT_USER_Avatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 签名：accessKey
     */
    private String accessKey;

    /**
     *签名：secreteKey
     */
    private String secreteKey;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}