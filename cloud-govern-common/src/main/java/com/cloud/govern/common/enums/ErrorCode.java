package com.cloud.govern.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author cloud-govern
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 方法不允许
     */
    METHOD_NOT_ALLOWED(405, "方法不允许"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // ========== 业务错误码 1000+ ==========

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /**
     * 账号已被禁用
     */
    ACCOUNT_DISABLED(1002, "账号已被禁用"),

    /**
     * Token 无效
     */
    TOKEN_INVALID(1003, "Token 无效"),

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(1004, "Token 已过期"),

    /**
     * 用户已存在
     */
    USER_EXISTS(1005, "用户已存在"),

    /**
     * 角色已存在
     */
    ROLE_EXISTS(1006, "角色已存在"),

    /**
     * 菜单已存在
     */
    MENU_EXISTS(1007, "菜单已存在"),

    /**
     * 数据已存在
     */
    DATA_EXISTS(1008, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(1009, "数据不存在"),

    /**
     * 操作失败
     */
    OPERATION_FAIL(1010, "操作失败");

    private final Integer code;
    private final String message;

}
