package com.lilac.service;

import com.lilac.domain.dto.space.analyze.*;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.User;
import com.lilac.domain.vo.analyze.*;

import java.util.List;

/**
 * 空间分析服务
 *
 * @author lilac
 */
public interface SpaceAnalyzeService {
    /**
     * 获取空间使用情况
     *
     * @param spaceUsageAnalyzeRequest 分析参数
     * @param loginUser                登录用户
     * @return 空间使用情况
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * 获取空间分类情况
     *
     * @param spaceCategoryAnalyzeRequest 分析参数
     * @param loginUser                   登录用户
     * @return 空间分类情况
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    /**
     * 获取空间标签情况
     *
     * @param spaceTagAnalyzeRequest 分析参数
     * @param loginUser              登录用户
     * @return 空间标签情况
     */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    /**
     * 获取空间图片大小情况
     *
     * @param spaceSizeAnalyzeRequest 分析参数
     * @param loginUser               登录用户
     * @return 空间大小情况
     */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
     * 获取空间用户情况
     *
     * @param spaceUserAnalyzeRequest 分析参数
     * @param loginUser               登录用户
     * @return 空间用户情况
     */
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    /**
     * 获取空间排行情况(管理员)
     *
     * @param spaceRankAnalyzeRequest 分析参数
     * @param loginUser               登录用户
     * @return 空间排行情况
     */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}
