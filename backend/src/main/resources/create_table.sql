-- 创建表
create database if not exists picture;
-- 切换表
use picture;

-- 用户表
CREATE TABLE if not exists `user`
(
    `id`           bigint                                           NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userAccount`  varchar(256) COLLATE utf8mb4_unicode_ci          NOT NULL COMMENT '账号',
    `userPassword` varchar(512) COLLATE utf8mb4_unicode_ci          NOT NULL COMMENT '密码',
    `userName`     varchar(256) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '用户昵称',
    `userAvatar`   varchar(1024) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '用户头像',
    `userProfile`  varchar(512) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '用户简介',
    `userRole`     varchar(256) COLLATE utf8mb4_unicode_ci          NOT NULL DEFAULT 'user' COMMENT '用户角色(user/admin)',
    `editTime`     datetime                                         NOT NULL DEFAULT (now()) COMMENT '编辑时间',
    `createTime`   datetime                                         NOT NULL DEFAULT (now()) COMMENT '创建时间',
    `updateTime`   datetime                                         NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`     tinyint                                          NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_userAccount` (`userAccount`),
    KEY `idx_userName` (`userName`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2016088683806998530
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 图片表
CREATE TABLE `picture` (
    `id`            bigint                                          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `url`           varchar(512) COLLATE utf8mb4_unicode_ci         NOT NULL COMMENT '图片url',
    `name`          varchar(128) COLLATE utf8mb4_unicode_ci         NOT NULL COMMENT '图片名称',
    `introduction`  varchar(512) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '简介',
    `category`      varchar(64) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '分类',
    `tags`          varchar(512) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '标签(JSON数组)',
    `picSize`       bigint                                          DEFAULT NULL COMMENT '图片体积',
    `picWidth`      int                                             DEFAULT NULL COMMENT '图片宽度',
    `picHeigh`      int                                             DEFAULT NULL COMMENT '图片高度',
    `picScale`      double                                          DEFAULT NULL COMMENT '图片宽高比例',
    `picFormat`     varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '图片格式',
    `userId`        bigint                                          NOT NULL COMMENT '创建用户id',
    `reviewStatus`  int                                             NOT NULL DEFAULT '0' COMMENT '审核状态：0-待审核; 1-通过; 2-拒绝',
    `reviewMessage` varchar(512) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '审核信息',
    `reviewerId`    bigint                                          DEFAULT NULL COMMENT '审核人 ID',
    `reviewTime`    datetime                                        DEFAULT NULL COMMENT '审核时间',
    `createTime`    datetime                                        NOT NULL DEFAULT (now()) COMMENT '创建时间',
    `editTime`      datetime                                        NOT NULL DEFAULT (now()) COMMENT '编辑时间',
    `updateTime`    datetime                                        NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_introduction` (`introduction`),
    KEY `idx_category` (`category`),
    KEY `idx_tags` (`tags`),
    KEY `idx_userId` (`userId`),
    KEY `idx_reviewStatus` (`reviewStatus`)
) ENGINE=InnoDB AUTO_INCREMENT=2021870572882911235 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;