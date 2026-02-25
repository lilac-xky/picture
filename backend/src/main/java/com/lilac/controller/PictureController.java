package com.lilac.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilac.annotation.AuthCheck;
import com.lilac.common.DeleteRequest;
import com.lilac.constant.UserConstant;
import com.lilac.domain.dto.picture.*;
import com.lilac.domain.entity.Picture;
import com.lilac.domain.entity.User;
import com.lilac.domain.result.Result;
import com.lilac.domain.vo.PictureTagCategory;
import com.lilac.domain.vo.PictureVO;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.enums.PictureReviewStatusEnum;
import com.lilac.exception.BusinessException;
import com.lilac.service.PictureService;
import com.lilac.service.UserService;
import com.lilac.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 图片控制器
 *
 * @author lilac
 */
@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private UserService userService;
    @Autowired
    private PictureService pictureService;

    /**
     * 上传图片
     *
     * @param multipartFile        文件
     * @param pictureUploadRequest 上传参数
     * @param request              请求
     * @return 上传结果
     */
    @PostMapping("/upload")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
                                           PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return Result.success(pictureVO);
    }

    /**
     * 删除图片
     *
     * @param deleteRequest 删除参数
     * @return 删除结果
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if(deleteRequest == null || deleteRequest.getId() <= 0){
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 仅本人和管理员可删除
        if(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
            throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED);
        }
        // 删除
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 更新图片
     *
     * @param pictureUpdateRequest 更新参数
     * @return 更新结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request) {
        if(pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0){
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 校验
        pictureService.validPicture(picture);
        // 判断是否有该图片
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 审核
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(picture, loginUser);
        // 更新
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 获取图片(仅管理员)
     *
     * @param id 图片id
     * @return 图片信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Picture> getPictureById(long id) {
        ThrowUtils.throwIf(id <= 0, HttpsCodeEnum.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        return Result.success(picture);
    }

    /**
     * 获取图片
     *
     * @param id 图片id
     * @return 图片信息
     */
    @GetMapping("/get/vo")
    public Result<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, HttpsCodeEnum.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        return Result.success(pictureService.getPictureVO(picture, request));
    }

    /**
     * 获取图片列表(仅管理员)
     *
     * @param pictureQueryRequest 查询参数
     * @return 图片列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));
        return Result.success(picturePage);
    }

    /**
     * 获取图片列表
     *
     * @param pictureQueryRequest 搜索参数
     * @return 图片列表
     */
    @PostMapping("/list/page/vo")
    public Result<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, HttpsCodeEnum.PARAMS_ERROR);
        // 非管理员只返回审核通过的图片
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));
        return Result.success(pictureService.getPictureVOPage(picturePage, request));
    }

    /**
     * 编辑图片
     *
     * @param pictureEditRequest 编辑参数
     * @return 编辑结果
     */
    @PostMapping("/edit")
    public Result<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        if(pictureEditRequest == null || pictureEditRequest.getId() <= 0){
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR);
        }
        // 转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        picture.setEditTime(new Date());
        // 校验
        pictureService.validPicture(picture);
        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(picture, loginUser);
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, HttpsCodeEnum.NOT_FOUND_ERROR);
        // 仅本人和管理员可编辑
        if(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
            throw new BusinessException(HttpsCodeEnum.UNAUTHORIZED);
        }
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, HttpsCodeEnum.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 获取标签分类
     *
     * @return 标签分类
     */
    @GetMapping("/tag_category")
    public Result<PictureTagCategory> listPictureTagCategory() {
        // todo 简约版本，后续有需求在完善
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("可爱","漂亮","高清","美女","校园","简约");
        List<String> categoryList = Arrays.asList("动漫","素材","卡通","校园","简约");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return Result.success(pictureTagCategory);
    }

    /**
     * 图片审核
     *
     * @param pictureReviewRequest 审核参数
     * @return 审核结果
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, HttpsCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return Result.success(true);
    }
}
