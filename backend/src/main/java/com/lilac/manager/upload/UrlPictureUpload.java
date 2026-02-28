package com.lilac.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.BusinessException;
import com.lilac.utils.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * url图片上传模板实现
 *
 * @author lilac
 */
@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    /**
     * 处理文件
     *
     * @param inputSource 输入源
     * @param file        文件
     */
    @Override
    protected void processFile(Object inputSource, File file) {
        String fileUrl = (String) inputSource;
        HttpUtil.downloadFile(fileUrl, file);
    }

    /**
     * 获取原始文件名
     *
     * @param inputSource 输入源
     * @return 原始文件名
     */
    @Override
    protected String getOriginalFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        int questionMarkIndex = fileUrl.indexOf("?");
        String path = questionMarkIndex > 0 ? fileUrl.substring(0, questionMarkIndex) : fileUrl;
        return FileUtil.getName(path);
    }

    /**
     * 校验图片
     *
     * @param inputSource 文件
     */
    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        // 检验非空
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), HttpsCodeEnum.PARAMS_ERROR, "文件地址不能为空");
        // 检验URL格式
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "文件地址格式错误");
        }
        // 检验URL协议
        ThrowUtils.throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"),
                HttpsCodeEnum.PARAMS_ERROR, "仅支持HTTP/HTTPS协议");
        // 发送HEAD请求验证文件是否存在
        try (HttpResponse httpResponse = HttpUtil.createRequest(Method.HEAD, fileUrl).execute()) {
            if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 文件存在，文件类型校验
            String contentType = httpResponse.header("Content-Type");
            if(StrUtil.isNotBlank(contentType)){
                final List<String> ALLOW_CONTENT_TYPE = List.of("image/png", "image/jpg", "image/jpeg", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPE.contains(contentType.toLowerCase()), HttpsCodeEnum.PARAMS_ERROR, "文件格式错误");
            }
            // 文件存在，文件大小校验
            String contentLengthStr = httpResponse.header("Content-Length");
            if(StrUtil.isNotBlank(contentLengthStr)){
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long ONE_M = 1024 * 1024;
                    ThrowUtils.throwIf(Long.parseLong(contentLengthStr) > 2 * ONE_M, HttpsCodeEnum.PARAMS_ERROR, "文件大小不能超过2M");
                } catch (NumberFormatException e){
                    throw new BusinessException(HttpsCodeEnum.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        }
    }
}
