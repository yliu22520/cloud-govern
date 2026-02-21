package com.cloud.govern.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色查询 DTO
 *
 * @author cloud-govern
 */
@Data
@Schema(description = "角色查询请求")
public class RoleQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符")
    private String roleKey;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "页码", example = "1")
    private Long pageNum;

    @Schema(description = "每页数量", example = "10")
    private Long pageSize;

}
