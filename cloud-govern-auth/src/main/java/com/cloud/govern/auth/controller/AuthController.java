package com.cloud.govern.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.cloud.govern.auth.dto.LoginDTO;
import com.cloud.govern.auth.dto.LoginVO;
import com.cloud.govern.auth.dto.UserInfoVO;
import com.cloud.govern.auth.service.AuthService;
import com.cloud.govern.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author cloud-govern
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(authService.login(dto));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        if (StpUtil.isLogin()) {
            authService.logout();
        }
        return R.ok();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user/info")
    public R<UserInfoVO> getUserInfo() {
        return R.ok(authService.getUserInfo());
    }

}
