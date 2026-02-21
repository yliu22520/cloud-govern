package com.cloud.govern.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单查询 DTO
 *
 * @author cloud-govern
 */
@Data
@Schema(description = "菜单查询请求")
public class MenuQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型：1目录 2菜单 3按钮")
    private Integer menuType;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

}
