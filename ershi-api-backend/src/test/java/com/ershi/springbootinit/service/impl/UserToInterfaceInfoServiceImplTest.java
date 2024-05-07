package com.ershi.springbootinit.service.impl;


import com.ershi.common.service.InnerUserToInterfaceInfoService;
import com.ershi.springbootinit.service.UserToInterfaceInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserToInterfaceInfoServiceImplTest {

    @Resource
    InnerUserToInterfaceInfoService innerUserToInterfaceInfoService;

    @Test
    public void invokeCount() {
        boolean b = innerUserToInterfaceInfoService.invokeCount(4, 1);
        Assertions.assertTrue(b);
    }
}