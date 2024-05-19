-- 创建库
create database if not exists ershi_api;

-- 切换库
use ershi_api;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    accessKey    varchar(256)                           null comment '密钥ID',
    secreteKey   varchar(256)                           null comment '密钥',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;

-- `interface_info`
create table if not exists `interface_info`
(
    `id`                   bigint                             not null auto_increment comment '主键' primary key,
    `name`                 varchar(256)                       not null comment '接口名称',
    `description`          varchar(256)                       not null comment '接口描述',
    `host`                 varchar(256)                       not null comment '接口主机',
    `url`                  varchar(512)                       not null comment '接口地址',
    `method`               varchar(256)                       not null comment '请求类型',
    `requestParams`        text comment '请求参数',
    `requestParamsExample` text comment '请求参数示例',
    `requestHeader`        text                               not null comment '请求头',
    `responseHeader`       text                               not null comment '响应头',
    `status`               int      default 0                 not null comment '接口状态 0 - 关闭  1- 开启',
    `userId`               bigint                             not null comment '创建人',
    `createTime`           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime`           datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete`             tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '`interface_info`' collate = utf8mb4_unicode_ci;


-- `用户调用接口关系表`
create table if not exists `user_to_interface_info`
(
    `id`              bigint                             not null auto_increment comment '主键' primary key,
    `userId`          bigint                             not null comment '调用用户 id',
    `interfaceInfoId` bigint                             not null comment '被调用接口 id',
    `invokeCount`     int      default 0                 not null comment '总调用次数',
    `leftInvokeCount` int      default 0                 not null comment '剩余调用次数',
    `status`          int      default 0                 not null comment '该用户是否允许调用该接口 0 - 正常  1- 禁用',
    `createTime`      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime`      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete`        tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户调用接口关系表' collate = utf8mb4_unicode_ci;


