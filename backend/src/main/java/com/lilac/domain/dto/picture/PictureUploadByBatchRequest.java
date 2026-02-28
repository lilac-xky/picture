package com.lilac.domain.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片批量上传请求
 *
 * @author lilac
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 文件名前缀
     */
    private String namePrefix;

    /**
     * 抓取数量
     */
    private Integer count = 10;
}
