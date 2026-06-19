package com.agricultural.service;

import com.agricultural.dto.LoginDTO;
import com.agricultural.dto.RegisterDTO;
import com.agricultural.entity.User;
import com.agricultural.vo.LoginVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取用户信息
     */
    User getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, User user);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 分页查询用户列表（管理员使用）
     */
    Page<User> getUserList(Integer pageNum, Integer pageSize, String keyword, String status);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, String status);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);
}

