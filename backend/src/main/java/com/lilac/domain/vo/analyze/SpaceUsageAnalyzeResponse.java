package com.lilac.domain.vo.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间资源使用情况响应类
 */
@Data
public class SpaceUsageAnalyzeResponse implements Serializable {

    /**
     * 已使用的空间大小
     */
    private Long usedSize;

    /**
     * 最大空间大小
     */
    private Long maxSize;

    /**
     * 空间使用比率
     */
    private Double sizeUsageRatio;

    /**
     * 已使用的图片数量
     */
    private Long usedCount;

    /**
     * 最大图片数量
     */
    private Long maxCount;

    /**
     * 图片数量占比
     */
    private Double countUsageRatio;

    private static final long serialVersionUID = 1L;
}
