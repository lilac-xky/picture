package com.lilac.controller;

import com.lilac.domain.dto.space.analyze.*;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.User;
import com.lilac.domain.result.Result;
import com.lilac.domain.vo.analyze.*;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.service.SpaceAnalyzeService;
import com.lilac.service.UserService;
import com.lilac.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 空间分析控制器
 * @author lilac
 */
@RestController
@RequestMapping("/space/analyze")
public class SpaceAnalyzeController {

    @Resource
    private SpaceAnalyzeService spaceAnalyzeService;
    @Resource
    private UserService userService;

    /**
     * 获取空间使用情况
     * @param spaceUsageAnalyzeRequest 空间使用情况请求
     * @return 空间使用情况响应
     */
    @PostMapping("/usage")
    public Result<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(@RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, HttpServletRequest request){
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        SpaceUsageAnalyzeResponse spaceUsageAnalyze = spaceAnalyzeService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest, loginUser);
        return Result.success(spaceUsageAnalyze);
    }

    /**
     * 获取空间图片分类使用情况
     * @param spaceCategoryAnalyzeRequest 空间使用情况请求
     * @return 空间使用情况响应
     */
    @PostMapping("/category")
    public Result<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(@RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, HttpServletRequest request){
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceCategoryAnalyzeResponse> spaceCategoryAnalyze = spaceAnalyzeService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest, loginUser);
        return Result.success(spaceCategoryAnalyze);
    }

    /**
     * 获取空间图片标签使用情况
     * @param spaceTagAnalyzeRequest 获取空间图片标签使用情况请求
     * @return 获取空间图片标签使用情况响应
     */
    @PostMapping("/tag")
    public Result<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(@RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceTagAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceTagAnalyze(spaceTagAnalyzeRequest, loginUser);
        return Result.success(resultList);
    }

    /**
     * 获取空间图片大小使用情况
     * @param spaceSizeAnalyzeRequest 获取空间图片大小使用情况请求
     * @return 获取空间图片大小使用情况响应
     */
    @PostMapping("/size")
    public Result<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(@RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceSizeAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest, loginUser);
        return Result.success(resultList);
    }

    /**
     * 获取空间图片用户使用情况
     * @param spaceUserAnalyzeRequest 获取空间图片用户使用情况请求
     * @return 获取空间图片用户使用情况响应
     */
    @PostMapping("/user")
    public Result<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(@RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceUserAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceUserAnalyze(spaceUserAnalyzeRequest, loginUser);
        return Result.success(resultList);
    }

    /**
     * 获取空间图片排行排行(管理员)
     * @param spaceRankAnalyzeRequest 获取空间图片排行使用情况请求
     * @return 获取空间图片排行使用情况响应
     */
    @PostMapping("/rank")
    public Result<List<Space>> getSpaceRankAnalyze(@RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<Space> resultList = spaceAnalyzeService.getSpaceRankAnalyze(spaceRankAnalyzeRequest, loginUser);
        return Result.success(resultList);
    }

}
