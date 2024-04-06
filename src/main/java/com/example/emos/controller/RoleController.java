package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.role.DeleteRoleByIdsForm;
import com.example.emos.controller.form.role.InsertRoleForm;
import com.example.emos.controller.form.role.SearchRoleByPageForm;
import com.example.emos.controller.form.role.UpdateRoleForm;
import com.example.emos.db.pojo.TbRole;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/role")
@Tag(name = "RoleController", description = "角色Web接口")
public class RoleController {

    @Resource
    private TbRoleService roleService;

    @GetMapping("/searchAllRole")
    @Operation(summary = "查询所有角色")
    @SaCheckLogin
    public R searchAllRole() {
        List<TbRole> roleList = roleService.query().select("id", "role_name").list()
                .stream().filter(tbRole -> !"超级管理员".equals(tbRole.getRoleName()))
                .collect(Collectors.toList());
        return R.ok().put("list", roleList);
    }

    @PostMapping("/searchRoleByPage")
    @Operation(summary = "分页查询角色信息")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public R searchRoleByPage(@Valid @RequestBody SearchRoleByPageForm searchRoleByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchRoleByPageForm).toBean(HashMap.class);
        return R.ok().put("page", roleService.searchRoleByPage(param));
    }

    @GetMapping("/searchById")
    @Operation(summary = "根据角色id查询角色信息")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestParam("roleId") Integer roleId) {
        if (roleId == null) {
            throw new EmosException("roleId不能为空");
        }
        if (roleId < 1) {
            throw new EmosException("roleId不能小于1");
        }
        return R.ok(roleService.searchById(roleId));
    }

    @PostMapping("/insert")
    @Operation(summary = "新增角色")
    @SaCheckPermission(value = {"ROOT", "ROLE:INSERT"}, mode = SaMode.OR)
    @EmosLog
    public R insert(@Valid @RequestBody InsertRoleForm insertRoleForm) {
        TbRole tbRole = JSONUtil.parse(insertRoleForm).toBean(TbRole.class);
        tbRole.setPermissions(Arrays.toString(insertRoleForm.getPermissions()));
        return R.ok().put("rows", roleService.insert(tbRole));
    }

    @PostMapping("/update")
    @Operation(summary = "修改角色信息")
    @SaCheckPermission(value = {"ROOT", "ROLE:UPDATE"}, mode = SaMode.OR)
    @EmosLog
    public R update(@Valid @RequestBody UpdateRoleForm updateRoleForm) {
        TbRole tbRole = new TbRole();
        tbRole.setId(updateRoleForm.getId());
        tbRole.setRoleName(updateRoleForm.getRoleName());
        tbRole.setPermissions(Arrays.toString(updateRoleForm.getPermissions()));
        tbRole.setDesc(updateRoleForm.getDesc());
        int rows = roleService.update(tbRole);
        if (rows == 1 && updateRoleForm.getChanged()) {
            // 用户修改成功，且用户修改了该角色的关联权限
            // 将与该角色关联的用户全部踢下线
            ArrayList<Integer> userIds = roleService.searchUserIdByRoleId(updateRoleForm.getId());
            userIds.forEach(StpUtil::logout);
        }
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteRoleByIds")
    @Operation(summary = "删除角色信息")
    @SaCheckPermission(value = {"ROOT", "ROLE:DELETE"}, mode = SaMode.OR)
    @EmosLog
    public R deleteRoleByIds(@Valid @RequestBody DeleteRoleByIdsForm deleteRoleByIdsForm) {
        return R.ok().put("rows", roleService.deleteRoleByIds(deleteRoleByIdsForm.getIds()));
    }
}
