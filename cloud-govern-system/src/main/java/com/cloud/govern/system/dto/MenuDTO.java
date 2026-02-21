package com.cloud.govern.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单创建/更新 DTO
 *
 * @author cloud-govern
 */
@Data
@Schema(description = "菜单创建/更新请求")
public class MenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称最多50个字符")
    private String menuName;

    @Schema(description = "菜单类型：1目录 2菜单 3按钮")
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    @Schema(description = "路由路径")
    @Size(max = 200, message = "路由路径最多200个字符")
    private String path;

    @Schema(description = "组件路径")
    @Size(max = 200, message = "组件路径最多200个字符")
    private String component;

    @Schema(description = "权限标识")
    @Size(max = 100, message = "权限标识最多100个字符")
    private String perms;

    @Schema(description = "图标")
    @Size(max = 100, message = "图标最多100个字符")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否可见：0否 1是")
    private Integer visible;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

}
