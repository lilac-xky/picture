package com.lilac.domain.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加空间成员请求
 */
@Data
public class SpaceUserAddRequest implements Serializable {
    /**
   * 空间id
   */
    private Long spaceId;

    /**
   * 用户id
   */
    private Long userId;

    /**
     * 角色
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}