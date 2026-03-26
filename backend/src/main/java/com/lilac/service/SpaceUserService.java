package com.lilac.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lilac.domain.dto.spaceuser.SpaceUserAddRequest;
import com.lilac.domain.dto.spaceuser.SpaceUserQueryRequest;
import com.lilac.domain.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lilac.domain.vo.SpaceUserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 空间角色关联服务
 *
* @author lilac
*/
public interface SpaceUserService extends IService<SpaceUser> {
    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest 添加参数
     * @return 添加结果
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 获取查询条件
     *
     * @param spaceUserQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取空间成员
     *
     * @param spaceUser 空间成员
     * @param request 请求
     * @return 空间信息
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间成员列表
     *
     * @param spaceUserList 空间成员列表
     * @return 空间信息
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

    /**
     * 校验空间成员
     *
     * @param spaceUser 空间成员
     * @param add 是否添加
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);
}
