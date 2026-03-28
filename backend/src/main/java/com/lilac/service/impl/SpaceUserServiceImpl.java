package com.lilac.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lilac.domain.dto.spaceuser.SpaceUserAddRequest;
import com.lilac.domain.dto.spaceuser.SpaceUserQueryRequest;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.SpaceUser;
import com.lilac.domain.entity.User;
import com.lilac.domain.vo.SpaceUserVO;
import com.lilac.domain.vo.SpaceVO;
import com.lilac.domain.vo.UserVO;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.enums.SpaceRoleEnum;
import com.lilac.exception.BusinessException;
import com.lilac.service.SpaceService;
import com.lilac.service.SpaceUserService;
import com.lilac.mapper.SpaceUserMapper;
import com.lilac.service.UserService;
import com.lilac.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 空间用户服务实现类
 *
 * @author lilac
 */
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserService {
    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private SpaceService spaceService;

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest 添加空间成员请求
     * @return 添加的空间成员
     */
    @Override
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        // 参数校验
        ThrowUtils.throwIf(spaceUserAddRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        validSpaceUser(spaceUser, true);
        // 添加空间成员
        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return spaceUser.getId();
    }

    /**
     * 获取查询条件
     *
     * @param spaceUserQueryRequest 查询条件
     * @return 查询条件
     */
    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 获取参数
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        // 创建查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "spaceRole", spaceRole);
        return queryWrapper;
    }

    /**
     * 获取空间成员
     *
     * @param spaceUser 空间成员
     * @param request   请求
     * @return 空间成员
     */
    @Override
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request) {
        // 对象转封装类
        SpaceUserVO spaceUserVO = SpaceUserVO.objToVo(spaceUser);
        // 关联查询用户信息
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceUserVO.setUser(userVO);
        }
        // 关联查询空间信息
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVO spaceVO = spaceService.getSpaceVO(space, request);
            spaceUserVO.setSpace(spaceVO);
        }
        return spaceUserVO;
    }

    /**
     * 获取空间成员列表
     *
     * @param spaceUserList 空间成员列表
     * @return 空间成员列表
     */
    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList) {
        // 判断是否为空
        if (CollUtil.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        // 封装对象列表
        List<SpaceUserVO> spaceUserVOList = spaceUserList.stream().map(SpaceUserVO::objToVo).collect(Collectors.toList());
        // 收集需要关联查询的用户id和空间id
        Set<Long> userIdSet = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        // 批量查询用户和空间
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceService.listByIds(spaceIdSet).stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 填充用户和空间信息
        spaceUserVOList.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            Long spaceId = spaceUserVO.getSpaceId();
            // 填充用户信息
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceUserVO.setUser(userService.getUserVO(user));
            // 填充空间信息
            Space space = null;
            if (spaceIdSpaceListMap.containsKey(spaceId)) {
                space = spaceIdSpaceListMap.get(spaceId).get(0);
            }
            spaceUserVO.setSpace(SpaceVO.objToVo(space));
        });
        return spaceUserVOList;
    }

    /**
     * 校验空间成员
     *
     * @param spaceUser 空间成员
     * @param add       是否添加
     */
    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowUtils.throwIf(spaceUser == null, HttpsCodeEnum.PARAMS_ERROR);
        // 创建时，空间、用户不能为空
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (add) {
            ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), HttpsCodeEnum.PARAMS_ERROR, "空间、用户不能为空");
            User user = userService.getById(userId);
            ThrowUtils.throwIf(user == null, HttpsCodeEnum.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, HttpsCodeEnum.NOT_FOUND_ERROR, "空间不存在");
        }
        // 校验空间角色
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "空间角色不存在");
        }
    }
}
