package com.ershi.springbootinit.model.dto.usertointerfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ershi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserToInterfaceInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 被调用接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer invokeCount;

    /**
     * 剩余调用次数
     */
    private Integer leftInvokeCount;

    /**
     * 该用户是否允许调用该接口 0 - 正常  1- 禁用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}