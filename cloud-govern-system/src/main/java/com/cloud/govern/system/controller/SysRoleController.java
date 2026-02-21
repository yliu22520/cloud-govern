package com.cloud.govern.system.controller;

import com.cloud.govern.common.result.PageResult;
import com.cloud.govern.common.result.R;
import com.cloud.govern.system.dto.RoleDTO;
import com.cloud.govern.system.dto.RoleQueryDTO;
import com.cloud.govern.system.dto.RoleVO;
import com.cloud.govern.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 *
 * @author cloud-govern
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 角色列表
     */
    @Operation(summary = "角色列表")
    @GetMapping("/list")
    public R<PageResult<RoleVO>> list(RoleQueryDTO query) {
        return R.ok(sysRoleService.list(query));
    }

    /**
     * 所有角色列表（不分页）
     */
    @Operation(summary = "所有角色列表")
    @GetMapping("/all")
    public R<List<RoleVO>> listAll() {
        return R.ok(sysRoleService.listAll());
    }

    /**
     * 角色详情
     */
    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    public R<RoleVO> getById(@Parameter(description = "角色ID") @PathVariable Long id) {
        return R.ok(sysRoleService.getById(id));
    }

    /**
     * 创建角色
     */
    @Operation(summary = "创建角色")
    @PostMapping
    public R<Long> create(@Valid @RequestBody RoleDTO dto) {
        return R.ok(sysRoleService.create(dto));
    }

    /**
     * 更新角色
     */
    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public R<Void> update(@Parameter(description = "角色ID") @PathVariable Long id,
                          @Valid @RequestBody RoleDTO dto) {
        sysRoleService.update(id, dto);
        return R.ok();
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "角色ID") @PathVariable Long id) {
        sysRoleService.delete(id);
        return R.ok();
    }

    /**
     * 更新状态
     */
    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@Parameter(description = "角色ID") @PathVariable Long id,
                                 @Parameter(description = "状态") @RequestParam Integer status) {
        sysRoleService.updateStatus(id, status);
        return R.ok();
    }

}
