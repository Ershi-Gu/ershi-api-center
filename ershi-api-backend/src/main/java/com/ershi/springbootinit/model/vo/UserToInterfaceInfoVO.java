package com.ershi.springbootinit.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ershi.springbootinit.model.entity.UserToInterfaceInfo;
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
public class UserToInterfaceInfoVO {

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
    public static UserToInterfaceInfo voToObj(UserToInterfaceInfoVO userToInterfaceInfoVO) {
        if (userToInterfaceInfoVO == null) {
            return null;
        }
        UserToInterfaceInfo userToInterfaceInfo = new UserToInterfaceInfo();
        BeanUtils.copyProperties(userToInterfaceInfoVO, userToInterfaceInfo);
        return userToInterfaceInfo;
    }

    /**
     * 对象转包装类
     *
     * @param interfaceinfo
     * @return
     */
    public static UserToInterfaceInfoVO objToVo(UserToInterfaceInfo userToInterfaceInfo) {
        if (userToInterfaceInfo == null) {
            return null;
        }
        UserToInterfaceInfoVO userToInterfaceInfoVO = new UserToInterfaceInfoVO();
        BeanUtils.copyProperties(userToInterfaceInfo, userToInterfaceInfoVO);
        return userToInterfaceInfoVO;
    }
}
