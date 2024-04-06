package com.example.emos.controller.form.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "修改密码表单类")
public class UpdatePasswordForm {

    @NotBlank(message = "password不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "password内容不正确")
    @Schema(description = "旧密码")
    private String password;

    @NotBlank(message = "newPassword不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "newPassword内容不正确")
    @Schema(description = "新密码")
    private String newPassword;
}
