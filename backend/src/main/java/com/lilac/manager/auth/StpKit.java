package com.lilac.manager.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * StpLogic 门面类，管理项目中所有的 StpLogic 账号体系
 */
@Component
public class StpKit {

    public static final String SPACE_TYPE = "space";

    /**
     * 默认的 StpLogic 对象，对应 StpUtil
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * 账号空间对应的 StpLogic 对象,管理space表的登录权限认证
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);
}