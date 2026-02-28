package com.lilac.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.BusinessException;
import com.lilac.utils.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 文件图片模板实现
 *
 * @author lilac
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate {
    /**
     * 处理文件
     *
     * @param inputSource 输入源
     * @param file        文件
     */
    @Override
    protected void processFile(Object inputSource, File file) throws IOException {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }

    /**
     * 获取原始文件名
     *
     * @param inputSource 输入源
     * @return 原始文件名
     */
    @Override
    protected String getOriginalFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    /**
     * 校验图片
     *
     * @param inputSource 文件
     */
    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        ThrowUtils.throwIf(multipartFile == null, HttpsCodeEnum.PARAMS_ERROR, "上传文件不能为空");
        // 校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, HttpsCodeEnum.PARAMS_ERROR, "文件大小不能超过2M");
        // 检验文件格式
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许的格式
        final List<String> ALLOW_FORMAT_LIST = List.of("png", "jpg", "jpeg", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), HttpsCodeEnum.PARAMS_ERROR, "文件格式错误");
    }
}
