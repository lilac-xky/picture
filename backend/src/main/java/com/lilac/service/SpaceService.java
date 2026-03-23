package com.lilac.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilac.domain.dto.space.SpaceAddRequest;
import com.lilac.domain.dto.space.SpaceQueryRequest;
import com.lilac.domain.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lilac.domain.entity.User;
import com.lilac.domain.vo.SpaceVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 空间服务
 * 
* @author lilac
*/
public interface SpaceService extends IService<Space> {
    /**
     * 添加空间
     *
     * @param spaceAddRequest 添加参数
     * @param loginUser 登录用户
     * @return 添加结果
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param spaceQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取空间
     *
     * @param space 空间
     * @param request 请求
     * @return 空间信息
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间分页
     *
     * @param spacePage 空间分页
     * @param request 请求
     * @return 空间信息
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 校验空间
     *
     * @param space 图片
     * @param add 是否添加
     */
    void validSpace(Space space, boolean add);

    /**
     * 填充空间信息
     *
     * @param space 空间
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 校验空间权限
     *
     * @param loginUser 登录用户
     * @param space     空间
     */
    void checkSpaceAuth(User loginUser, Space space);
}
