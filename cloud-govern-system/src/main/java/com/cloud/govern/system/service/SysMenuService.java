package com.cloud.govern.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.govern.common.enums.ErrorCode;
import com.cloud.govern.common.exception.BusinessException;
import com.cloud.govern.system.dto.MenuDTO;
import com.cloud.govern.system.dto.MenuQueryDTO;
import com.cloud.govern.system.dto.MenuVO;
import com.cloud.govern.system.entity.SysMenu;
import com.cloud.govern.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务
 *
 * @author cloud-govern
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    /**
     * 查询菜单列表（树形结构）
     *
     * @param query 查询条件
     * @return 菜单树
     */
    public List<MenuVO> list(MenuQueryDTO query) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getMenuName()), SysMenu::getMenuName, query.getMenuName());
        wrapper.eq(query.getMenuType() != null, SysMenu::getMenuType, query.getMenuType());
        wrapper.eq(query.getStatus() != null, SysMenu::getStatus, query.getStatus());
        wrapper.orderByAsc(SysMenu::getSort);

        List<SysMenu> menus = sysMenuMapper.selectList(wrapper);

        // 构建树形结构
        return buildMenuTree(menus, 0L);
    }

    /**
     * 查询所有菜单列表（不分页，树形结构）
     *
     * @return 菜单树
     */
    public List<MenuVO> listAll() {
        List<SysMenu> menus = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .orderByAsc(SysMenu::getSort)
        );
        return buildMenuTree(menus, 0L);
    }

    /**
     * 查询菜单详情
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    public MenuVO getById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return convertToVO(menu);
    }

    /**
     * 创建菜单
     *
     * @param dto 菜单信息
     * @return 菜单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(MenuDTO dto) {
        // 检查菜单名称是否已存在
        Long count = sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getMenuName, dto.getMenuName())
                        .eq(dto.getParentId() != null, SysMenu::getParentId, dto.getParentId() != null ? dto.getParentId() : 0L)
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.MENU_EXISTS);
        }

        // 创建菜单
        SysMenu menu = new SysMenu();
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPerms(dto.getPerms());
        menu.setIcon(dto.getIcon());
        menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        sysMenuMapper.insert(menu);

        log.info("创建菜单成功: {}", menu.getMenuName());
        return menu.getId();
    }

    /**
     * 更新菜单
     *
     * @param id  菜单ID
     * @param dto 菜单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MenuDTO dto) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        // 不能将父菜单设置为自己或自己的子菜单
        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(id)) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "父菜单不能是自己");
            }
            // 检查是否是自己的子菜单
            if (isChildMenu(id, dto.getParentId())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "父菜单不能是自己的子菜单");
            }
        }

        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPerms(dto.getPerms());
        menu.setIcon(dto.getIcon());
        if (dto.getSort() != null) {
            menu.setSort(dto.getSort());
        }
        if (dto.getVisible() != null) {
            menu.setVisible(dto.getVisible());
        }
        if (dto.getStatus() != null) {
            menu.setStatus(dto.getStatus());
        }

        sysMenuMapper.updateById(menu);

        log.info("更新菜单成功: {}", menu.getMenuName());
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        // 检查是否有子菜单
        Long count = sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getParentId, id)
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "存在子菜单，不能删除");
        }

        // 逻辑删除菜单
        sysMenuMapper.deleteById(id);

        log.info("删除菜单成功: {}", menu.getMenuName());
    }

    /**
     * 更新菜单状态
     *
     * @param id     菜单ID
     * @param status 状态
     */
    public void updateStatus(Long id, Integer status) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        menu.setStatus(status);
        sysMenuMapper.updateById(menu);

        log.info("更新菜单状态成功: {}, status: {}", menu.getMenuName(), status);
    }

    /**
     * 检查是否是子菜单
     *
     * @param parentId 父菜单ID
     * @param childId  子菜单ID
     * @return 是否是子菜单
     */
    private boolean isChildMenu(Long parentId, Long childId) {
        List<SysMenu> children = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getParentId, parentId)
        );
        for (SysMenu child : children) {
            if (child.getId().equals(childId)) {
                return true;
            }
            if (isChildMenu(child.getId(), childId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构建菜单树
     *
     * @param menus    菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<MenuVO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();

        // 按父ID分组
        Map<Long, List<SysMenu>> menuMap = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        // 获取指定父ID的子菜单
        List<SysMenu> children = menuMap.get(parentId);
        if (children == null) {
            return tree;
        }

        for (SysMenu menu : children) {
            MenuVO vo = convertToVO(menu);
            // 递归构建子菜单
            vo.setChildren(buildMenuTree(menus, menu.getId()));
            tree.add(vo);
        }

        return tree;
    }

    /**
     * 转换为VO
     *
     * @param menu 菜单实体
     * @return 菜单VO
     */
    private MenuVO convertToVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuType(menu.getMenuType());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setPerms(menu.getPerms());
        vo.setIcon(menu.getIcon());
        vo.setSort(menu.getSort());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        vo.setCreateTime(menu.getCreateTime());
        vo.setUpdateTime(menu.getUpdateTime());
        return vo;
    }

}
