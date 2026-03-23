package com.lilac.domain.vo.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 空间标签分析响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceTagAnalyzeResponse implements Serializable {

    /**
     * 标签
     */
    private String tag;

    /**
     * 数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}