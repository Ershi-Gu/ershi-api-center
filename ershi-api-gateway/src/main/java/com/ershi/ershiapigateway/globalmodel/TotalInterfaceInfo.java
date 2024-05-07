package com.ershi.ershiapigateway.globalmodel;

import com.ershi.common.model.entity.InterfaceInfo;
import com.ershi.common.service.InnerInterfaceInfoService;
import lombok.Data;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于缓存所有接口信息到网关
 * @author Ershi
 * @date 2024/05/07
 */
@Component
public class TotalInterfaceInfo {

    private final Map<String, InterfaceInfo> interfaceInfoMap = new HashMap<>();

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @PostConstruct
    public void init() {
        //系统启动中。。。加载 interfaceInfoMap
        List<InterfaceInfo> allInterfaceInfo = innerInterfaceInfoService.getAllInterfaceInfo();
        for (InterfaceInfo interfaceInfo : allInterfaceInfo) {
            interfaceInfoMap.put(interfaceInfo.getId().toString(), interfaceInfo);
        }
    }

    public Map<String, InterfaceInfo> getInterfaceInfoMap(){
        return interfaceInfoMap;
    }

}
