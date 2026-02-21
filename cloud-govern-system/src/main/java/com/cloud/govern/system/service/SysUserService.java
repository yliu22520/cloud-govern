package com.cloud.govern.system.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.govern.common.enums.ErrorCode;
import com.cloud.govern.common.exception.BusinessException;
import com.cloud.govern.common.result.PageResult;
import com.cloud.govern.common.utils.PageUtils;
import com.cloud.govern.system.dto.UserDTO;
import com.cloud.govern.system.dto.UserQueryDTO;
import com.cloud.govern.system.dto.UserVO;
import com.cloud.govern.system.entity.SysUser;
import com.cloud.govern.system.entity.SysUserRole;
import com.cloud.govern.system.mapper.SysRoleMapper;
import com.cloud.govern.system.mapper.SysUserMapper;
import com.cloud.govern.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务
 *
 * @author cloud-govern
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    /**
     * 分页查询用户列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResult<UserVO> list(UserQueryDTO query) {
        Page<SysUser> page = PageUtils.buildPage(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getUsername()), SysUser::getUsername, query.getUsername());
        wrapper.like(StringUtils.isNotBlank(query.getNickname()), SysUser::getNickname, query.getNickname());
        wrapper.like(StringUtils.isNotBlank(query.getPhone()), SysUser::getPhone, query.getPhone());
        wrapper.eq(query.getStatus() != null, SysUser::getStatus, query.getStatus());
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = sysUserMapper.selectPage(page, wrapper);

        List<UserVO> list = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    /**
     * 查询用户详情
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public UserVO getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return convertToVO(user);
    }

    /**
     * 创建用户
     *
     * @param dto 用户信息
     * @return 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserDTO dto) {
        // 检查用户名是否已存在
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_EXISTS);
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        sysUserMapper.insert(user);

        // 保存用户角色关联
        saveUserRoles(user.getId(), dto.getRoleIds());

        log.info("创建用户成功: {}", user.getUsername());
        return user.getId();
    }

    /**
     * 更新用户
     *
     * @param id  用户ID
     * @param dto 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UserDTO dto) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        // 检查用户名是否已存在（排除自己）
        if (StringUtils.isNotBlank(dto.getUsername()) && !dto.getUsername().equals(user.getUsername())) {
            Long count = sysUserMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUsername, dto.getUsername())
                            .ne(SysUser::getId, id)
            );
            if (count > 0) {
                throw new BusinessException(ErrorCode.USER_EXISTS);
            }
            user.setUsername(dto.getUsername());
        }

        // 更新密码
        if (StringUtils.isNotBlank(dto.getPassword())) {
            user.setPassword(BCrypt.hashpw(dto.getPassword()));
        }

        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }

        sysUserMapper.updateById(user);

        // 更新用户角色关联
        if (dto.getRoleIds() != null) {
            sysUserRoleMapper.deleteByUserId(id);
            saveUserRoles(id, dto.getRoleIds());
        }

        log.info("更新用户成功: {}", user.getUsername());
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        // 逻辑删除用户
        sysUserMapper.deleteById(id);

        // 删除用户角色关联
        sysUserRoleMapper.deleteByUserId(id);

        log.info("删除用户成功: {}", user.getUsername());
    }

    /**
     * 重置密码
     *
     * @param id       用户ID
     * @param password 新密码
     */
    public void resetPassword(Long id, String password) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        user.setPassword(BCrypt.hashpw(password));
        sysUserMapper.updateById(user);

        log.info("重置用户密码成功: {}", user.getUsername());
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态
     */
    public void updateStatus(Long id, Integer status) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        user.setStatus(status);
        sysUserMapper.updateById(user);

        log.info("更新用户状态成功: {}, status: {}", user.getUsername(), status);
    }

    /**
     * 保存用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }
    }

    /**
     * 转换为VO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        // 查询角色ID列表
        List<Long> roleIds = sysUserMapper.selectRoleIdsByUserId(user.getId());
        vo.setRoleIds(roleIds);

        // 查询角色名称列表
        if (!roleIds.isEmpty()) {
            List<String> roleNames = sysRoleMapper.selectRolesByUserId(user.getId())
                    .stream()
                    .map(role -> role.getRoleName())
                    .collect(Collectors.toList());
            vo.setRoleNames(roleNames);
        } else {
            vo.setRoleNames(new ArrayList<>());
        }

        return vo;
    }

}
