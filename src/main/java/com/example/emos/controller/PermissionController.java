package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.example.emos.common.util.R;
import com.example.emos.service.TbPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/permission")
@Tag(name = "PermissionController", description = "权限web接口")
public class PermissionController {

    @Resource
    private TbPermissionService permissionService;

    @GetMapping("/searchAllPermission")
    @Operation(summary = "查询所有的权限信息")
    @SaCheckPermission(value = {"ROOT", "ROLE:INSERT","ROLE:UPDATE"}, mode = SaMode.OR)
    public R searchAllPermission() {
        return R.ok().put("list", permissionService.searchAllPermission());
    }
}
