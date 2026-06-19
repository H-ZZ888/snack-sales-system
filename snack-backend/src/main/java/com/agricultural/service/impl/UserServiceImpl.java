package com.agricultural.service.impl;

import com.agricultural.dto.LoginDTO;
import com.agricultural.dto.RegisterDTO;
import com.agricultural.entity.User;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.UserMapper;
import com.agricultural.service.UserService;
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
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, registerDTO.getPhone());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setStatus("正常");

        this.save(user);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if ("禁用".equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "user");

        // 返回登录信息
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setUserType("user");

        return loginVO;
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 清空密码
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUserInfo(Long userId, User user) {
        User existUser = this.getById(userId);
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 只允许更新部分字段
        existUser.setNickname(user.getNickname());
        existUser.setEmail(user.getEmail());
        existUser.setAvatar(user.getAvatar());

        this.updateById(existUser);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public Page<User> getUserList(Integer pageNum, Integer pageSize, String keyword, String status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        
        // 状态筛选
        if (StringUtils.hasText(status)) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = this.page(page, wrapper);
        
        // 清空密码
        result.getRecords().forEach(user -> user.setPassword(null));
        
        return result;
    }

    @Override
    public void updateUserStatus(Long userId, String status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!this.removeById(userId)) {
            throw new BusinessException("删除失败");
        }
    }
}

