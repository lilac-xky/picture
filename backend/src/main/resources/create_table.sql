-- 创建表
create database if not exists picture;
-- 切换表
use picture;

-- 用户表
create table user
(
    id           bigint auto_increment primary key,
    userAccount  varchar(256)                 not null comment '账号',
    userPassword varchar(512)                 not null comment '密码',
    userName     varchar(256)                 null comment '用户昵称',
    userAvatar   varchar(1024)                null comment '用户头像',
    userProfile  varchar(512)                 null comment '用户简介',
    userRole     varchar(256) default 'user'  not null comment '用户角色(user/admin)',
    editTime     datetime     default (now()) not null comment '编辑时间',
    createTime   datetime     default (now()) not null comment '创建时间',
    updateTime   datetime     default (now()) not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0       not null comment '是否删除',
    constraint uk_userAccount
        unique (userAccount)
);

create index idx_userName
    on user (userName);