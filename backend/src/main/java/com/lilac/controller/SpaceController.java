package com.lilac.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilac.annotation.AuthCheck;
import com.lilac.common.DeleteRequest;
import com.lilac.constant.UserConstant;
import com.lilac.domain.dto.space.*;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.User;
import com.lilac.domain.result.Result;
import com.lilac.domain.vo.SpaceVO;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.enums.SpaceLevelEnum;
import com.lilac.exception.BusinessException;
import com.lilac.service.SpaceService;
import com.lilac.service.UserService;
import com.lilac.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 空间控制器
 *
 * @author lilac
 */
@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private UserService userService;
    @Resource
    private SpaceService spaceService;

    /**
     * 添加空间
     *
     * @param spaceAddRequest 添加参数
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceAddRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long newId = spaceService.addSpace(spaceAddRequest, loginUser);
        return Result.success(newId);
    }

    /**
     * 删除空间
     *
     * @param deleteRequest 删除参数
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 仅本人和管理员可删除
        if (!oldSpace.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED);
        }
        // 删除
        boolean result = spaceService.removeById(id);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 更新空间(管理员)
     *
     * @param spaceUpdateRequest 更新参数
     * @return 更新结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest, HttpServletRequest request) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);
        // 自动填充
        spaceService.fillSpaceBySpaceLevel(space);
        // 校验
        spaceService.validSpace(space, false);
        // 判断是否有该空间
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 更新
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 获取空间(仅管理员)
     *
     * @param id 空间id
     * @return 空间信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Space> getSpaceById(long id) {
        ThrowUtils.throwIf(id <= 0, HttpsCodeEnum.PARAMS_ERROR);
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        return Result.success(space);
    }

    /**
     * 获取空间
     *
     * @param id 空间id
     * @return 空间信息
     */
    @GetMapping("/get/vo")
    public Result<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, HttpsCodeEnum.PARAMS_ERROR);
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        return Result.success(spaceService.getSpaceVO(space, request));
    }

    /**
     * 获取空间列表(仅管理员)
     *
     * @param spaceQueryRequest 查询参数
     * @return 空间列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        Page<Space> spacePage = spaceService.page(new Page<>(current, size), spaceService.getQueryWrapper(spaceQueryRequest));
        return Result.success(spacePage);
    }

    /**
     * 获取空间列表
     *
     * @param spaceQueryRequest 搜索参数
     * @return 空间列表
     */
    @PostMapping("/list/page/vo")
    public Result<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest, HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, HttpsCodeEnum.PARAMS_ERROR);
        Page<Space> spacePage = spaceService.page(new Page<>(current, size), spaceService.getQueryWrapper(spaceQueryRequest));
        return Result.success(spaceService.getSpaceVOPage(spacePage, request));
    }

    /**
     * 编辑空间
     *
     * @param spaceEditRequest 编辑参数
     * @return 编辑结果
     */
    @PostMapping("/edit")
    public Result<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        // 转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceEditRequest, space);
        space.setEditTime(new Date());
        // 自动填充
        spaceService.fillSpaceBySpaceLevel(space);
        // 校验
        spaceService.validSpace(space, false);
        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        long id = spaceEditRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 仅本人和管理员可编辑
        if (!oldSpace.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED);
        }
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 获取空间等级列表
     *
     * @return 空间等级列表
     */
    @GetMapping("/list/level")
    public Result<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelsList = Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()
                )).collect(Collectors.toList());
        return Result.success(spaceLevelsList);
    }
}
