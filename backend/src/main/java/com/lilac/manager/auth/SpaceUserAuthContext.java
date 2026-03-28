package com.lilac.manager.auth;

import com.lilac.domain.entity.Picture;
import com.lilac.domain.entity.Space;
import com.lilac.domain.entity.SpaceUser;
import lombok.Data;

/**
 * 授权上下文
 */
@Data
public class SpaceUserAuthContext {
    /**
     * 临时参数
     */
    private Long id;

    /**
     * 图片id
     */
    private Long pictureId;

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 空间成员id
     */
    private Long spaceUserId;

    /**
     * 图片信息
     */
    private Picture picture;

    /**
     * 空间信息
     */
    private Space space;

    /**
     * 空间成员信息
     */
    private SpaceUser spaceUser;
}