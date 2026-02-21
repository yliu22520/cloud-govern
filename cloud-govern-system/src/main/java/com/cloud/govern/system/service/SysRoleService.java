package com.cloud.govern.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.govern.common.enums.ErrorCode;
import com.cloud.govern.common.exception.BusinessException;
import com.cloud.govern.common.result.PageResult;
import com.cloud.govern.common.utils.PageUtils;
import com.cloud.govern.system.dto.RoleDTO;
import com.cloud.govern.system.dto.RoleQueryDTO;
import com.cloud.govern.system.dto.RoleVO;
import com.cloud.govern.system.entity.SysRole;
import com.cloud.govern.system.entity.SysRoleMenu;
import com.cloud.govern.system.mapper.SysMenuMapper;
import com.cloud.govern.system.mapper.SysRoleMapper;
import com.cloud.govern.system.mapper.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务
 *
 * @author cloud-govern
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 分页查询角色列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResult<RoleVO> list(RoleQueryDTO query) {
        Page<SysRole> page = PageUtils.buildPage(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getRoleName()), SysRole::getRoleName, query.getRoleName());
        wrapper.like(StringUtils.isNotBlank(query.getRoleKey()), SysRole::getRoleKey, query.getRoleKey());
        wrapper.eq(query.getStatus() != null, SysRole::getStatus, query.getStatus());
        wrapper.orderByAsc(SysRole::getSort);
        wrapper.orderByDesc(SysRole::getCreateTime);

        Page<SysRole> result = sysRoleMapper.selectPage(page, wrapper);

        List<RoleVO> list = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    /**
     * 查询所有角色列表（不分页）
     *
     * @return 角色列表
     */
    public List<RoleVO> listAll() {
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, 1)
                        .orderByAsc(SysRole::getSort)
        );
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 查询角色详情
     *
     * @param id 角色ID
     * @return 角色信息
     */
    public RoleVO getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return convertToVO(role);
    }

    /**
     * 创建角色
     *
     * @param dto 角色信息
     * @return 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(RoleDTO dto) {
        // 检查角色Key是否已存在
        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getRoleKey, dto.getRoleKey())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.ROLE_EXISTS);
        }

        // 创建角色
        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleKey(dto.getRoleKey());
        role.setDescription(dto.getDescription());
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        role.setSort(dto.getSort() != null ? dto.getSort() : 0);

        sysRoleMapper.insert(role);

        // 保存角色菜单关联
        saveRoleMenus(role.getId(), dto.getMenuIds());

        log.info("创建角色成功: {}", role.getRoleName());
        return role.getId();
    }

    /**
     * 更新角色
     *
     * @param id  角色ID
     * @param dto 角色信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, RoleDTO dto) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        // 检查角色Key是否已存在（排除自己）
        if (StringUtils.isNotBlank(dto.getRoleKey()) && !dto.getRoleKey().equals(role.getRoleKey())) {
            Long count = sysRoleMapper.selectCount(
                    new LambdaQueryWrapper<SysRole>()
                            .eq(SysRole::getRoleKey, dto.getRoleKey())
                            .ne(SysRole::getId, id)
            );
            if (count > 0) {
                throw new BusinessException(ErrorCode.ROLE_EXISTS);
            }
        }

        role.setRoleName(dto.getRoleName());
        role.setRoleKey(dto.getRoleKey());
        role.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        if (dto.getSort() != null) {
            role.setSort(dto.getSort());
        }

        sysRoleMapper.updateById(role);

        // 更新角色菜单关联
        if (dto.getMenuIds() != null) {
            sysRoleMenuMapper.deleteByRoleId(id);
            saveRoleMenus(id, dto.getMenuIds());
        }

        log.info("更新角色成功: {}", role.getRoleName());
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        // 逻辑删除角色
        sysRoleMapper.deleteById(id);

        // 删除角色菜单关联
        sysRoleMenuMapper.deleteByRoleId(id);

        log.info("删除角色成功: {}", role.getRoleName());
    }

    /**
     * 更新角色状态
     *
     * @param id     角色ID
     * @param status 状态
     */
    public void updateStatus(Long id, Integer status) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        role.setStatus(status);
        sysRoleMapper.updateById(role);

        log.info("更新角色状态成功: {}, status: {}", role.getRoleName(), status);
    }

    /**
     * 保存角色菜单关联
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    private void saveRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }

        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            sysRoleMenuMapper.insert(roleMenu);
        }
    }

    /**
     * 转换为VO
     *
     * @param role 角色实体
     * @return 角色VO
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleKey(role.getRoleKey());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setSort(role.getSort());
        vo.setCreateTime(role.getCreateTime());
        vo.setUpdateTime(role.getUpdateTime());

        // 查询菜单ID列表
        List<Long> menuIds = sysMenuMapper.selectMenuIdsByRoleId(role.getId());
        vo.setMenuIds(menuIds);

        return vo;
    }

}
