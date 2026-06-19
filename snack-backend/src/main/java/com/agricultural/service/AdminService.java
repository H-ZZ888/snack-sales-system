package com.agricultural.service;

import com.agricultural.dto.LoginDTO;
import com.agricultural.dto.RegisterDTO;
import com.agricultural.entity.SysAdmin;
import com.agricultural.vo.LoginVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 管理员服务接口
 */
public interface AdminService extends IService<SysAdmin> {

    /**
     * 管理员注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 管理员登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取管理员信息
     */
    SysAdmin getAdminInfo(Long adminId);

    /**
     * 更新管理员信息
     */
    void updateAdminInfo(Long adminId, SysAdmin admin);

    /**
     * 修改密码
     */
    void updatePassword(Long adminId, String oldPassword, String newPassword);

    /**
     * 分页查询管理员列表
     */
    Page<SysAdmin> getAdminList(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 删除管理员
     */
    void deleteAdmin(Long adminId);
}

