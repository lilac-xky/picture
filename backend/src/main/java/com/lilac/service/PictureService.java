package com.lilac.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lilac.domain.dto.picture.PictureQueryRequest;
import com.lilac.domain.dto.picture.PictureReviewRequest;
import com.lilac.domain.dto.picture.PictureUploadRequest;
import com.lilac.domain.entity.Picture;
import com.lilac.domain.entity.User;
import com.lilac.domain.vo.PictureVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片服务
 *
 * @author lilac
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile 文件
     * @param pictureUploadRequest 上传参数
     * @param loginUser 登录用户
     * @return 上传结果
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片
     *
     * @param picture 图片
     * @param request 请求
     * @return 图片信息
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片分页
     *
     * @param picturePage 图片分页
     * @param request 请求
     * @return 图片信息
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 校验图片
     *
     * @param picture 图片
     */
    void validPicture(Picture picture);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest 图片审核参数
     * @param loginUser 登录用户
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     *
     * @param picture 图片
     * @param loginUser 登录用户
     */
    void fillReviewParams(Picture picture, User loginUser);
}
