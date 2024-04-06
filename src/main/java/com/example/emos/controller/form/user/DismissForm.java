package com.example.emos.controller.form.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "设置员工离职表单")
public class DismissForm {
    @NotNull(message = "userId不能为空")
    @Min(value = 1, message = "userId不能小于1")
    private Integer userId;

    @NotNull(message = "assigneeId不能为空")
    @Min(value = 1, message = "assigneeId不能小于1")
    private Integer assigneeId;
}