package com.lilac.domain.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用空间分析请求参数
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 是否查询公开空间
     */
    private boolean queryPublic;

    /**
     * 是否查询所有空间
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}