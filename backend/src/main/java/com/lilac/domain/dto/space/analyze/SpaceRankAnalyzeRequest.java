package com.lilac.domain.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取空间排行(管理员)
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 获取前N名
     */
    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}