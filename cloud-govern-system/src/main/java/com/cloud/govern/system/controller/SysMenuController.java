package com.cloud.govern.system.controller;

import com.cloud.govern.common.result.R;
import com.cloud.govern.system.dto.MenuDTO;
import com.cloud.govern.system.dto.MenuQueryDTO;
import com.cloud.govern.system.dto.MenuVO;
import com.cloud.govern.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 *
 * @author cloud-govern
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 菜单列表（树形结构）
     */
    @Operation(summary = "菜单列表")
    @GetMapping("/list")
    public R<List<MenuVO>> list(MenuQueryDTO query) {
        return R.ok(sysMenuService.list(query));
    }

    /**
     * 所有菜单列表（不分页，树形结构）
     */
    @Operation(summary = "所有菜单列表")
    @GetMapping("/all")
    public R<List<MenuVO>> listAll() {
        return R.ok(sysMenuService.listAll());
    }

    /**
     * 菜单详情
     */
    @Operation(summary = "菜单详情")
    @GetMapping("/{id}")
    public R<MenuVO> getById(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return R.ok(sysMenuService.getById(id));
    }

    /**
     * 创建菜单
     */
    @Operation(summary = "创建菜单")
    @PostMapping
    public R<Long> create(@Valid @RequestBody MenuDTO dto) {
        return R.ok(sysMenuService.create(dto));
    }

    /**
     * 更新菜单
     */
    @Operation(summary = "更新菜单")
    @PutMapping("/{id}")
    public R<Void> update(@Parameter(description = "菜单ID") @PathVariable Long id,
                          @Valid @RequestBody MenuDTO dto) {
        sysMenuService.update(id, dto);
        return R.ok();
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "菜单ID") @PathVariable Long id) {
        sysMenuService.delete(id);
        return R.ok();
    }

    /**
     * 更新状态
     */
    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@Parameter(description = "菜单ID") @PathVariable Long id,
                                 @Parameter(description = "状态") @RequestParam Integer status) {
        sysMenuService.updateStatus(id, status);
        return R.ok();
    }

}
