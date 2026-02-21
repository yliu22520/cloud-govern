package com.cloud.govern.system.controller;

import com.cloud.govern.common.result.PageResult;
import com.cloud.govern.common.result.R;
import com.cloud.govern.system.dto.UserDTO;
import com.cloud.govern.system.dto.UserQueryDTO;
import com.cloud.govern.system.dto.UserVO;
import com.cloud.govern.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author cloud-govern
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 用户列表
     */
    @Operation(summary = "用户列表")
    @GetMapping("/list")
    public R<PageResult<UserVO>> list(UserQueryDTO query) {
        return R.ok(sysUserService.list(query));
    }

    /**
     * 用户详情
     */
    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public R<UserVO> getById(@Parameter(description = "用户ID") @PathVariable Long id) {
        return R.ok(sysUserService.getById(id));
    }

    /**
     * 创建用户
     */
    @Operation(summary = "创建用户")
    @PostMapping
    public R<Long> create(@Valid @RequestBody UserDTO dto) {
        return R.ok(sysUserService.create(dto));
    }

    /**
     * 更新用户
     */
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public R<Void> update(@Parameter(description = "用户ID") @PathVariable Long id,
                          @Valid @RequestBody UserDTO dto) {
        sysUserService.update(id, dto);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        sysUserService.delete(id);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @PutMapping("/{id}/password")
    public R<Void> resetPassword(@Parameter(description = "用户ID") @PathVariable Long id,
                                  @Parameter(description = "新密码") @RequestParam String password) {
        sysUserService.resetPassword(id, password);
        return R.ok();
    }

    /**
     * 更新状态
     */
    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@Parameter(description = "用户ID") @PathVariable Long id,
                                 @Parameter(description = "状态") @RequestParam Integer status) {
        sysUserService.updateStatus(id, status);
        return R.ok();
    }

}
