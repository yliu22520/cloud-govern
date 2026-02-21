package com.cloud.govern.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建/更新 DTO
 *
 * @author cloud-govern
 */
@Data
@Schema(description = "用户创建/更新请求")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度为2-50个字符")
    private String username;

    @Schema(description = "密码")
    @Size(min = 6, max = 100, message = "密码长度为6-100个字符")
    private String password;

    @Schema(description = "昵称")
    @Size(max = 50, message = "昵称最多50个字符")
    private String nickname;

    @Schema(description = "邮箱")
    @Size(max = 100, message = "邮箱最多100个字符")
    private String email;

    @Schema(description = "手机号")
    @Size(max = 20, message = "手机号最多20个字符")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "角色ID列表")
    private java.util.List<Long> roleIds;

}
