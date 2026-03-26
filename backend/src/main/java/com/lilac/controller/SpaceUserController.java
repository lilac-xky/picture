package com.lilac.controller;

import cn.hutool.core.util.ObjectUtil;
import com.lilac.common.DeleteRequest;
import com.lilac.domain.dto.spaceuser.SpaceUserAddRequest;
import com.lilac.domain.dto.spaceuser.SpaceUserEditRequest;
import com.lilac.domain.dto.spaceuser.SpaceUserQueryRequest;
import com.lilac.domain.entity.SpaceUser;
import com.lilac.domain.entity.User;
import com.lilac.domain.result.Result;
import com.lilac.domain.vo.SpaceUserVO;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.BusinessException;
import com.lilac.service.SpaceUserService;
import com.lilac.service.UserService;
import com.lilac.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 空间角色控制器
 *
 * @author lilac
 */
@RestController
@RequestMapping("/spaceUser")
public class SpaceUserController {
    @Resource
    private SpaceUserService spaceUserService;
    @Resource
    private UserService userService;

    /**
     * 添加空间角色
     *
     * @param spaceUserAddRequest 添加空间角色请求
     * @return 添加的空间角色id
     */
    @PostMapping("/add")
    public Result<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest) {
        ThrowUtils.throwIf(spaceUserAddRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        long id = spaceUserService.addSpaceUser(spaceUserAddRequest);
        return Result.success(id);
    }

    /**
     * 删除空间角色
     *
     * @param deleteRequest 删除空间角色请求
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ThrowUtils.throwIf(oldSpaceUser == null, HttpsCodeEnum.NOT_FOUND_ERROR);

        boolean result = spaceUserService.removeById(id);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 获取空间角色
     *
     * @param spaceUserQueryRequest 获取空间角色请求
     * @return 空间角色
     */
    @PostMapping("/get")
    public Result<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
        // 参数校验
        ThrowUtils.throwIf(spaceUserQueryRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), HttpsCodeEnum.PARAMS_ERROR);
        // 获取空间角色
        SpaceUser spaceUser = spaceUserService.getOne(spaceUserService.getQueryWrapper(spaceUserQueryRequest));
        ThrowUtils.throwIf(spaceUser == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        return Result.success(spaceUser);
    }

    /**
     * 获取空间角色列表
     *
     * @param spaceUserQueryRequest 获取空间角色列表请求
     * @return 空间角色列表
     */
    @PostMapping("/list")
    public Result<List<SpaceUserVO>> listSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserQueryRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        List<SpaceUser> spaceUserList = spaceUserService.list(spaceUserService.getQueryWrapper(spaceUserQueryRequest));
        return Result.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }

    /**
     * 编辑空间角色
     *
     * @param spaceUserEditRequest 编辑空间角色请求
     * @return 编辑结果
     */
    @PostMapping("/edit")
    public Result<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest, HttpServletRequest request) {
        if (spaceUserEditRequest == null || spaceUserEditRequest.getId() <= 0) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        // 装换
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserEditRequest, spaceUser);
        // 校验
        spaceUserService.validSpaceUser(spaceUser, false);
        // 判断是否存在
        long id = spaceUserEditRequest.getId();
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ThrowUtils.throwIf(oldSpaceUser == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 更新
        boolean result = spaceUserService.updateById(spaceUser);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 查询用户加入的团队空间列表
     *
     * @return 当前用户加入的团队空间列表
     */
    @PostMapping("/list/my")
    public Result<List<SpaceUserVO>> listMyTeamSpace(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        SpaceUserQueryRequest spaceUserQueryRequest = new SpaceUserQueryRequest();
        spaceUserQueryRequest.setUserId(loginUser.getId());
        List<SpaceUser> spaceUserList = spaceUserService.list(spaceUserService.getQueryWrapper(spaceUserQueryRequest));
        return Result.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }
}
