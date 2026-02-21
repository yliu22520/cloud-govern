package com.cloud.govern.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色创建/更新 DTO
 *
 * @author cloud-govern
 */
@Data
@Schema(description = "角色创建/更新请求")
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称最多50个字符")
    private String roleName;

    @Schema(description = "角色权限字符")
    @NotBlank(message = "角色权限字符不能为空")
    @Size(max = 50, message = "角色权限字符最多50个字符")
    private String roleKey;

    @Schema(description = "描述")
    @Size(max = 255, message = "描述最多255个字符")
    private String description;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;

}
