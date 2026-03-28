package com.lilac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lilac.domain.dto.space.SpaceAddRequest;
import com.lilac.domain.dto.space.SpaceQueryRequest;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.SpaceUser;
import com.lilac.domain.entity.User;
import com.lilac.domain.vo.SpaceVO;
import com.lilac.domain.vo.UserVO;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.enums.SpaceLevelEnum;
import com.lilac.enums.SpaceRoleEnum;
import com.lilac.enums.SpaceTypeEnum;
import com.lilac.exception.BusinessException;
import com.lilac.service.SpaceService;
import com.lilac.mapper.SpaceMapper;
import com.lilac.service.SpaceUserService;
import com.lilac.service.UserService;
import com.lilac.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 空间服务实现
 *
 * @author lilac
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {

    @Resource
    private UserService userService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SpaceUserService spaceUserService;

    /**
     * 添加空间
     *
     * @param spaceAddRequest 添加空间参数
     * @param loginUser       登录用户
     * @return 添加的空间id
     */
    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 填充默认参数
        Space space = new Space();
        BeanUtil.copyProperties(spaceAddRequest, space);
        // 默认参数
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (spaceAddRequest.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        if (spaceAddRequest.getSpaceType() == null) {
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        // 自动填充
        this.fillSpaceBySpaceLevel(space);
        // 校验参数
        validSpace(space, true);
        // 校验权限，非管理员只能创建普通级别空间
        Long userId = loginUser.getId();
        space.setUserId(userId);
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED, "非管理员只能创建普通空间");
        }
        // 控制同一用户只能创建一个私有空间及一个团队空间
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(transactionStatus -> {
                boolean exists = this.lambdaQuery()
                        .eq(Space::getUserId, userId)
                        .eq(Space::getSpaceType, space.getSpaceType())
                        .exists();
                ThrowUtils.throwIf(exists, HttpsCodeEnum.OPERATION_ERROR, "每个用户只能创建一个空间");
                // 创建空间
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR, "创建空间失败");
                // 创建成功后，如果是团队空间，关联新增成员
                if (SpaceTypeEnum.TEAM.getValue() == spaceAddRequest.getSpaceType()) {
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setUserId(userId);
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    result = spaceUserService.save(spaceUser);
                    ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR, "创建团队成员记录失败");
                }
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }

    /**
     * 获取查询条件
     *
     * @param spaceQueryRequest 查询参数
     * @return 查询条件
     */
    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询参数
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();

        // 创建查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 获取空间包装类
     *
     * @param space   空间
     * @param request 请求
     * @return 空间信息
     */
    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 获取用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0L) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    /**
     * 获取空间分页包装类
     *
     * @param spacePage 分页参数
     * @param request   请求
     * @return 分页信息
     */
    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 获取用户信息
        List<SpaceVO> spaceVOList = spaceList.stream().map(SpaceVO::objToVo).collect(Collectors.toList());
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 填充用户信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    /**
     * 校验空间
     *
     * @param space 空间
     * @param add   是否为创建
     */
    @Override
    public void validSpace(Space space, boolean add) {
        ThrowUtils.throwIf(space == null, HttpsCodeEnum.PARAMS_ERROR);
        // 获取参数
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        Integer spaceType = space.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);
        // 创建时，空间名称和空间级别不能为空
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "空间级别不能为空");
            }
            if (spaceType == null) {
                throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "空间类型不能为空");
            }
        }
        // 修改时，空间名称和空间级别不能为空
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "空间级别不存在");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "空间名称过长");
        }
        // 修改时，空间类型不能为空
        if (spaceType != null && spaceTypeEnum == null) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "空间类型不存在");
        }
    }

    /**
     * 填充空间信息
     *
     * @param space 空间
     */
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    /**
     * 校验空间权限
     *
     * @param loginUser 登录用户
     * @param space     空间
     */
    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        // 仅管理员和本人可操作
        if (!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED);
        }
    }
}




