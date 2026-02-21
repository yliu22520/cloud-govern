package com.cloud.govern.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询 DTO
 *
 * @author cloud-govern
 */
@Data
@Schema(description = "用户查询请求")
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "页码", example = "1")
    private Long pageNum;

    @Schema(description = "每页数量", example = "10")
    private Long pageSize;

}
