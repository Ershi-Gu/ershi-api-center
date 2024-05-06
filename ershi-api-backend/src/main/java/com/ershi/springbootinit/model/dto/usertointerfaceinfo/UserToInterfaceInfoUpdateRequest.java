package com.ershi.springbootinit.model.dto.usertointerfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 * 
 */
@Data
public class UserToInterfaceInfoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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