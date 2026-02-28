package com.lilac.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.lilac.config.CosClientConfig;
import com.lilac.domain.dto.file.UploadPictureResult;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.BusinessException;
import com.lilac.manager.CosManager;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 文件管理模板
 *
 * @author lilac
 */
@Slf4j
public abstract class PictureUploadTemplate {
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param inputSource    文件
     * @param uploadPathPrefix 上传路径前缀
     * @return 上传结果
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 校验图片
        validPicture(inputSource);
        // 生成文件名
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginalFilename(inputSource);
        String uploadFilename = String.format("%s.%s", uuid, FileUtil.getSuffix(originalFilename));
        // 构建上传路径
        String projectName = "lilac-picture";
        String uploadPath = String.format(projectName + "/%s/%s/%s", uploadPathPrefix, DateUtil.format(new Date(),"yyyy/MM"), uploadFilename);
        // 解析结果并返回
        File file = null;
        try {
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源
            processFile(inputSource, file);
            // 上传图片到对象服务
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            return buildResult(imageInfo, uploadPath, originalFilename, file);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(HttpsCodeEnum.SYSTEM_ERROR, "上传失败");
        } finally {
            // 临时文件清理
            this.deleteTempFile(file);
        }
    }

    /**
     * 处理文件来源
     *
     * @param inputSource 文件来源
     */
    protected abstract void processFile(Object inputSource, File file) throws IOException;

    /**
     * 获取原始文件名
     *
     * @param inputSource 文件来源
     * @return 文件名
     */
    protected abstract String getOriginalFilename(Object inputSource);

    /**
     * 校验图片
     *
     * @param inputSource 文件来源
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 构建上传结果
     *
     * @param imageInfo      图片信息
     * @param uploadPath     上传路径
     * @param originalFilename 原始文件名
     * @param file           文件
     * @return 上传结果
     */
    private UploadPictureResult buildResult(ImageInfo imageInfo, String uploadPath, String originalFilename, File file) {
        // 计算宽高比
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        // 返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        uploadPictureResult.setPickName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeigh(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        return uploadPictureResult;
    }

    /**
     * 删除临时文件
     *
     * @param file 文件
     */
    public void deleteTempFile(File file) {
        if (file != null) {
            boolean delete = file.delete();
            if (!delete) {
                log.error("file delete failed, filePath = " + file.getAbsoluteFile());
            }
        }
    }

}
