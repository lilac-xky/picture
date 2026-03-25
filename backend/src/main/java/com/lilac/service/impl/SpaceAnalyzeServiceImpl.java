package com.lilac.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lilac.domain.dto.space.analyze.*;
import com.lilac.domain.entity.Picture;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.User;
import com.lilac.domain.vo.analyze.*;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.BusinessException;
import com.lilac.service.PictureService;
import com.lilac.service.SpaceAnalyzeService;
import com.lilac.service.SpaceService;
import com.lilac.service.UserService;
import com.lilac.utils.ThrowUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 空间分析服务实现类
 *
 * @author lilac
 */
@Service
public class SpaceAnalyzeServiceImpl implements SpaceAnalyzeService {

    @Resource
    private UserService userService;
    @Resource
    private SpaceService spaceService;
    @Resource
    private PictureService pictureService;

    /**
     * 检查空间分析权限
     *
     * @param spaceAnalyzeRequest 空间分析请求
     * @param loginUser           登录用户
     */
    private void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser) {
        boolean queryPublic = spaceAnalyzeRequest.isQueryPublic();
        boolean queryAll = spaceAnalyzeRequest.isQueryAll();
        // 全空间分析或公共图库权限校验：仅管理员
        if (queryAll || queryPublic) {
            ThrowUtils.throwIf(!userService.isAdmin(loginUser), HttpsCodeEnum.UNAUTHORIZED);
        } else {
            // 分析特定空间，仅本人和管理员
            Long spaceId = spaceAnalyzeRequest.getSpaceId();
            ThrowUtils.throwIf(spaceId == null, HttpsCodeEnum.PARAMS_ERROR);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, HttpsCodeEnum.NOT_FOUND_ERROR, "空间不存在");
            spaceService.checkSpaceAuth(loginUser, space);
        }
    }

    /**
     * 填充空间分析查询条件
     *
     * @param spaceAnalyzeRequest 空间分析请求
     * @param queryWrapper        查询条件
     */
    private void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest, QueryWrapper<Picture> queryWrapper) {
        boolean queryAll = spaceAnalyzeRequest.isQueryAll();
        // 全空间分析
        if (queryAll) {
            return;
        }
        boolean queryPublic = spaceAnalyzeRequest.isQueryPublic();
        // 公共图库分析
        if (queryPublic) {
            queryWrapper.isNull("spaceId");
            return;
        }
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        // 特定空间分析
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "未指定查询范围");
    }

    /**
     * 获取空间使用情况
     *
     * @param spaceUsageAnalyzeRequest 空间分析请求
     * @param loginUser                登录用户
     * @return 空间分析结果
     */
    @Override
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser) {
        SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
        // 全空间或公共空间，需从Picture中查询
        if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
            // 校验参数
            checkSpaceAnalyzeAuth(spaceUsageAnalyzeRequest, loginUser);
            // 统计图库空间的使用
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            fillAnalyzeQueryWrapper(spaceUsageAnalyzeRequest, queryWrapper);
            List<Object> pictureObjList = pictureService.getBaseMapper().selectObjs(queryWrapper);
            long usedSize = pictureObjList.stream().mapToLong(obj -> (Long) obj).sum();
            long usedCount = pictureObjList.size();
            // 封装返回结果
            spaceUsageAnalyzeResponse.setUsedSize(usedSize);
            spaceUsageAnalyzeResponse.setUsedCount(usedCount);
            spaceUsageAnalyzeResponse.setMaxSize(null);
            spaceUsageAnalyzeResponse.setSizeUsageRatio(null);
            spaceUsageAnalyzeResponse.setMaxCount(null);
            spaceUsageAnalyzeResponse.setCountUsageRatio(null);
        } else {
            // 特定空间，从Space中查询
            Long spaceId = spaceUsageAnalyzeRequest.getSpaceId();
            ThrowUtils.throwIf(spaceId == null || spaceId <= 0, HttpsCodeEnum.PARAMS_ERROR);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, HttpsCodeEnum.NOT_FOUND_ERROR, "空间不存在");
            // 校验权限
            checkSpaceAnalyzeAuth(spaceUsageAnalyzeRequest, loginUser);
            spaceUsageAnalyzeResponse.setUsedSize(space.getTotalSize());
            spaceUsageAnalyzeResponse.setMaxSize(space.getMaxSize());
            spaceUsageAnalyzeResponse.setUsedCount(space.getTotalCount());
            spaceUsageAnalyzeResponse.setMaxCount(space.getMaxCount());
            double sizeUsageRatio = NumberUtil.round(space.getTotalSize() * 100.0 / space.getMaxSize(), 2).doubleValue();
            double countUsageRatio = NumberUtil.round(space.getTotalCount() * 100.0 / space.getMaxCount(), 2).doubleValue();
            spaceUsageAnalyzeResponse.setSizeUsageRatio(sizeUsageRatio);
            spaceUsageAnalyzeResponse.setCountUsageRatio(countUsageRatio);
        }
        return spaceUsageAnalyzeResponse;
    }

    /**
     * 获取空间分类情况
     *
     * @param spaceCategoryAnalyzeRequest 空间分析请求
     * @param loginUser                   登录用户
     * @return 空间分析结果
     */
    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        // 校验权限
        checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest, loginUser);
        // 查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest, queryWrapper);
        // 分组查询
        queryWrapper.select("category AS category", "COUNT(*) AS count", "SUM(picSize) AS totalSize").groupBy("category");
        return pictureService.getBaseMapper().selectMaps(queryWrapper).stream()
                .map(result -> {
                    String category = result.get("category") != null ? result.get("category").toString() : "未分类";
                    Long count = ((Number) result.get("count")).longValue();
                    Long totalSize = ((Number) result.get("totalSize")).longValue();
                    return new SpaceCategoryAnalyzeResponse(category, count, totalSize);
                }).collect(Collectors.toList());
    }

    /**
     * 获取空间标签情况
     *
     * @param spaceTagAnalyzeRequest 空间分析请求
     * @param loginUser              登录用户
     * @return 空间分析结果
     */
    @Override
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        // 校验权限
        checkSpaceAnalyzeAuth(spaceTagAnalyzeRequest, loginUser);
        // 查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);
        // 分组查询
        queryWrapper.select("tags");
        List<String> tagsJsonList = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .filter(ObjUtil::isNotNull)
                .map(Object::toString)
                .collect(Collectors.toList());
        // 解析标签
        Map<String, Long> tagCountMap = tagsJsonList.stream()
                .flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
        return tagCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .map(entry -> new SpaceTagAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 获取空间图片大小情况
     *
     * @param spaceSizeAnalyzeRequest 空间分析请求
     * @param loginUser               登录用户
     * @return 空间分析结果
     */
    @Override
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        // 校验权限
        checkSpaceAnalyzeAuth(spaceSizeAnalyzeRequest, loginUser);
        // 查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);
        // 分组查询
        queryWrapper.select("picSize");
        List<Long> picSizes = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .map(size -> ((Number) size).longValue())
                .collect(Collectors.toList());
        // 定义分段范围
        Map<String, Long> sizeRanges = new LinkedHashMap<>();
        sizeRanges.put("<100KB", picSizes.stream().filter(size -> size < 100 * 1024).count());
        sizeRanges.put("100KB-500KB", picSizes.stream().filter(size -> size >= 100 * 1024 && size < 500 * 1024).count());
        sizeRanges.put("500KB-1MB", picSizes.stream().filter(size -> size >= 500 * 1024 && size < 1 * 1024 * 1024).count());
        sizeRanges.put(">1MB", picSizes.stream().filter(size -> size >= 1 * 1024 * 1024).count());
        return sizeRanges.entrySet().stream()
                .map(entry -> new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 获取空间用户情况
     *
     * @param spaceUserAnalyzeRequest 空间分析请求
     * @param loginUser               登录用户
     * @return 空间分析结果
     */
    @Override
    public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        // 校验权限
        checkSpaceAnalyzeAuth(spaceUserAnalyzeRequest, loginUser);
        // 创建查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        Long userId = spaceUserAnalyzeRequest.getUserId();
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        fillAnalyzeQueryWrapper(spaceUserAnalyzeRequest, queryWrapper);
        // 分组查询
        String timeDimension = spaceUserAnalyzeRequest.getTimeDimension();
        switch (timeDimension) {
            case "day":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m-%d') AS period", "COUNT(*) AS count");
                break;
            case "week":
                queryWrapper.select("YEARWEEK(createTime) AS period", "COUNT(*) AS count");
                break;
            case "month":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m') AS period", "COUNT(*) AS count");
                break;
            default:
                throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "不支持的时间维度");
        }
        // 分组查询
        queryWrapper.groupBy("period").orderByAsc("period");
        List<Map<String, Object>> queryResult = pictureService.getBaseMapper().selectMaps(queryWrapper);
        return queryResult.stream()
                .map(result -> {
                    String period = result.get("period").toString();
                    Long count = ((Number) result.get("count")).longValue();
                    return new SpaceUserAnalyzeResponse(period, count);
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取空间排行情况(管理员)
     *
     * @param spaceRankAnalyzeRequest 空间排行请求
     * @param loginUser               登录用户
     * @return 空间排行结果
     */
    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        ThrowUtils.throwIf(!userService.isAdmin(loginUser), HttpsCodeEnum.UNAUTHORIZED, "无权查看空间排行");
        // 创建查询条件
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "spaceName", "userId", "totalSize")
                .orderByDesc("totalSize")
                .last("LIMIT " + spaceRankAnalyzeRequest.getTopN());
        return spaceService.list(queryWrapper);
    }
}
