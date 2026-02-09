package com.lilac.domain.dto.file;

import lombok.Data;

/**
 * 上传图片结果
 */
@Data
public class UploadPictureResult {
    /**
     * 图片url
     */
    private String url;

    /**
     * 图片名称
     */
    private String pickName;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeigh;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

}
