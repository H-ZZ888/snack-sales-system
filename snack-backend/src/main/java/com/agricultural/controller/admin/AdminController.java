package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.dto.LoginDTO;
import com.agricultural.dto.RegisterDTO;
import com.agricultural.entity.SysAdmin;
import com.agricultural.service.AdminService;
import com.agricultural.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        adminService.register(registerDTO);
        return Result.success();
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = adminService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 获取管理员信息
     */
    @GetMapping("/info")
    public Result<SysAdmin> getAdminInfo(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        SysAdmin admin = adminService.getAdminInfo(adminId);
        return Result.success(admin);
    }

    /**
     * 更新管理员信息
     */
    @PutMapping("/info")
    public Result<Void> updateAdminInfo(HttpServletRequest request, @RequestBody SysAdmin admin) {
        Long adminId = (Long) request.getAttribute("userId");
        adminService.updateAdminInfo(adminId, admin);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(HttpServletRequest request, @RequestBody Map<String, String> params) {
        Long adminId = (Long) request.getAttribute("userId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        adminService.updatePassword(adminId, oldPassword, newPassword);
        return Result.success();
    }

}

