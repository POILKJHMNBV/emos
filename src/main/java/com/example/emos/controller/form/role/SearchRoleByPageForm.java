package com.example.emos.controller.form.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "分页查询角色信息表单类")
@Data
public class SearchRoleByPageForm {
    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    @Schema(description = "页数")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 10, max = 50, message = "length必须在10~50之间")
    @Schema(description = "每页记录数")
    private Integer length;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,10}$", message = "roleName内容不正确")
    @Schema(description = "角色名称")
    private String roleName;
}
