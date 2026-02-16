package com.cloud.govern.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.govern.auth.dto.LoginDTO;
import com.cloud.govern.auth.dto.LoginVO;
import com.cloud.govern.auth.dto.UserInfoVO;
import com.cloud.govern.auth.entity.SysUser;
import com.cloud.govern.auth.mapper.SysUserMapper;
import com.cloud.govern.common.enums.ErrorCode;
import com.cloud.govern.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务
 *
 * @author cloud-govern
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;

    /**
     * 登录
     *
     * @param dto 登录请求
     * @return 登录响应
     */
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );

        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }

        // 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }

        // 校验状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 登录
        StpUtil.login(user.getId());

        // 构建响应
        LoginVO vo = new LoginVO();
        vo.setAccessToken(StpUtil.getTokenValue());
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());

        // TODO: 查询用户角色和权限
        List<String> roles = new ArrayList<>();
        roles.add("admin");
        vo.setRoles(roles);
        vo.setPermissions(new ArrayList<>());

        log.info("用户登录成功: {}", user.getUsername());
        return vo;
    }

    /**
     * 退出登录
     */
    public void logout() {
        Long userId = StpUtil.getLoginIdAsLong();
        StpUtil.logout();
        log.info("用户退出登录: {}", userId);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    public UserInfoVO getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());

        // TODO: 查询用户角色和权限
        List<String> roles = new ArrayList<>();
        roles.add("admin");
        vo.setRoles(roles);
        vo.setPermissions(new ArrayList<>());

        return vo;
    }

}
