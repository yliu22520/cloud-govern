package com.cloud.govern.common.constant;

/**
 * 安全相关常量
 *
 * @author cloud-govern
 */
public class SecurityConstants {

    private SecurityConstants() {
    }

    /**
     * Token 请求头名称
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 用户ID Key
     */
    public static final String USER_ID_KEY = "userId";

    /**
     * 用户名 Key
     */
    public static final String USERNAME_KEY = "username";

    /**
     * 角色 Key
     */
    public static final String ROLE_KEY = "roles";

    /**
     * 权限 Key
     */
    public static final String PERMISSION_KEY = "permissions";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 超级管理员角色
     */
    public static final String SUPER_ADMIN_ROLE = "super_admin";

    /**
     * 管理员角色
     */
    public static final String ADMIN_ROLE = "admin";

}
