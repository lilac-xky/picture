package com.lilac.manager.websocket;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.lilac.constant.UserConstant;
import com.lilac.domain.entity.Picture;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.User;
import com.lilac.enums.SpaceTypeEnum;
import com.lilac.manager.auth.SpaceUserAuthManager;
import com.lilac.manager.auth.model.SpaceUserPermissionConstant;
import com.lilac.service.PictureService;
import com.lilac.service.SpaceService;
import com.lilac.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * 握手拦截器
 */
@Component
@Slf4j
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @Resource
    private SessionRepository<? extends Session> sessionRepository;

    /**
     * 握手前拦截
     */
    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            // 请求中获取参数
            String pictureId = servletRequest.getParameter("pictureId");
            if (StrUtil.isBlank(pictureId)) {
                log.error("缺少图片参数，拒绝握手");
                return false;
            }
            // 从 URL 参数中获取 token（实际上是 Session ID）
            String token = servletRequest.getParameter("token");
            if (StrUtil.isBlank(token)) {
                log.error("缺少 token 参数，拒绝握手");
                return false;
            }
            // 通过 Session ID 获取 Session 并恢复用户信息
            User loginUser = null;
            try {
                Session session = sessionRepository.findById(token);
                if (session != null) {
                    Object userObj = session.getAttribute(UserConstant.USER_LOGIN_STATE);
                    if (userObj instanceof User) {
                        User user = (User) userObj;
                        // 从数据库中查询最新的用户信息
                        loginUser = userService.getById(user.getId());
                    }
                }
            } catch (Exception e) {
                log.error("Session 验证失败: {}", e.getMessage());
            }
            if (ObjUtil.isEmpty(loginUser)) {
                log.error("用户未登录或 token 无效，拒绝握手");
                return false;
            }
            // 获取图片
            Picture picture = pictureService.getById(pictureId);
            if (picture == null) {
                log.error("图片不存在，拒绝握手");
                return false;
            }
            // 获取空间
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null) {
                space = spaceService.getById(spaceId);
                if (space == null) {
                    log.error("空间不存在，拒绝握手");
                    return false;
                }
                if (space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()) {
                    log.info("不是团队空间，拒绝握手");
                    return false;
                }
            }
            // 获取权限
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("没有图片编辑权限，拒绝握手");
                return false;
            }
            // 添加属性
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId)); 
        }
        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {
    }
}