package com.agricultural.service.impl;

import com.agricultural.dto.LoginDTO;
import com.agricultural.dto.RegisterDTO;
import com.agricultural.entity.SysAdmin;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.SysAdminMapper;
import com.agricultural.service.AdminService;
import com.agricultural.util.JwtUtil;
import com.agricultural.vo.LoginVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements AdminService {

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmin::getUsername, registerDTO.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 创建管理员
        SysAdmin admin = new SysAdmin();
        BeanUtils.copyProperties(registerDTO, admin);
        admin.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        this.save(admin);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询管理员
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmin::getUsername, loginDTO.getUsername());
        SysAdmin admin = this.getOne(wrapper);

        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "admin");

        // 返回登录信息
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(admin.getId());
        loginVO.setUsername(admin.getUsername());
        loginVO.setNickname(admin.getNickname());
        loginVO.setAvatar(admin.getAvatar());
        loginVO.setUserType("admin");

        return loginVO;
    }

    @Override
    public SysAdmin getAdminInfo(Long adminId) {
        SysAdmin admin = this.getById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        // 清空密码
        admin.setPassword(null);
        return admin;
    }

    @Override
    public void updateAdminInfo(Long adminId, SysAdmin admin) {
        SysAdmin existAdmin = this.getById(adminId);
        if (existAdmin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 只允许更新部分字段
        existAdmin.setNickname(admin.getNickname());
        existAdmin.setEmail(admin.getEmail());
        existAdmin.setPhone(admin.getPhone());
        existAdmin.setAvatar(admin.getAvatar());

        this.updateById(existAdmin);
    }

    @Override
    public void updatePassword(Long adminId, String oldPassword, String newPassword) {
        SysAdmin admin = this.getById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        admin.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(admin);
    }

    @Override
    public Page<SysAdmin> getAdminList(Integer pageNum, Integer pageSize, String keyword) {
        Page<SysAdmin> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysAdmin::getUsername, keyword)
                    .or().like(SysAdmin::getNickname, keyword);
        }
        
        wrapper.orderByDesc(SysAdmin::getCreateTime);
        Page<SysAdmin> result = this.page(page, wrapper);
        
        // 清空密码
        result.getRecords().forEach(admin -> admin.setPassword(null));
        
        return result;
    }

    @Override
    public void deleteAdmin(Long adminId) {
        if (!this.removeById(adminId)) {
            throw new BusinessException("删除失败");
        }
    }
}

