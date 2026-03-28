package com.lilac.manager.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 空间成员角色
 */
@Data
public class SpaceUserRole implements Serializable {

    /**
     * 角色key
     */
    private String key;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色权限
     */
    private List<String> permissions;

    /**
     * 角色描述
     */
    private String description;

    private static final long serialVersionUID = 1L;
}