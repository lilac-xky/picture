package com.lilac.domain.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询空间成员请求
 */
@Data
public class SpaceUserQueryRequest implements Serializable {

    /**
    * id
    */
    private Long id;

    /**
    * 空间id
    */
    private Long spaceId;

    /**
    * 用户id
    */
    private Long userId;

    /**
    * 空间角色
    */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}