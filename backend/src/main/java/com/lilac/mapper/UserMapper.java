package com.lilac.mapper;

import com.lilac.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author lilac
*/
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户账户查询用户数量
     *
     * @param userAccount 用户账户
     * @return 用户数量
     */
    @Select("select count(*) from user where userAccount = #{userAccount} limit 1")
    long countAllByAccount(String userAccount);
}




