package com.lilac.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.SpaceUser;
import com.lilac.domain.entity.User;
import com.lilac.enums.SpaceRoleEnum;
import com.lilac.enums.SpaceTypeEnum;
import com.lilac.manager.auth.model.SpaceUserAuthConfig;
import com.lilac.manager.auth.model.SpaceUserRole;
import com.lilac.service.SpaceUserService;
import com.lilac.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 空间成员管理
 */
@Component
public class SpaceUserAuthManager {

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    @Resource
    private UserService userService;
    @Resource
    private SpaceUserService spaceUserService;

    static {
        String json = ResourceUtil.readUtf8Str("biz/SpaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 获取空间成员权限
     *
     * @param spaceUserRole 空间成员角色
     * @return 权限列表
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (StrUtil.isBlank(spaceUserRole)) {
            return new ArrayList<>();
        }
        SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles()
                .stream()
                .filter(item -> item.getKey().equals(spaceUserRole))
                .findFirst()
                .orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();
    }

    /**
     * 获取空间成员权限列表
     *
     * @param space     空间
     * @param loginUser 登录用户
     * @return 权限列表
     */
    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            }
            return new ArrayList<>();
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        // 根据空间获取权限
        switch (spaceTypeEnum) {
            case PRIVATE:
                // 私有空间
                if (space.getUserId().equals(loginUser.getId()) || userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间,查询SpaceUser并获取角色和权限
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }

}
