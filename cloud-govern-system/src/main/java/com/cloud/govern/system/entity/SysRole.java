package com.cloud.govern.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.govern.common.entity.BaseEntityWithDelete;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 *
 * @author cloud-govern
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "角色实体")
public class SysRole extends BaseEntityWithDelete {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符")
    private String roleKey;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

}
