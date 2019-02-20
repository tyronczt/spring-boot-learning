package org.spring.springboot.dao.master;

import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.User;

/**
 * 用户 DAO 接口类
 */
public interface UserDao {

    /**
     * 根据用户名获取用户信息
     *
     * @param userName
     * @return
     */
    User findByName(@Param("userName") String userName);
}
