package com.lilac.domain.vo.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 空间用户分析响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserAnalyzeResponse implements Serializable {

    /**
     * 时间段
     */
    private String period;

    /**
     * 数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}