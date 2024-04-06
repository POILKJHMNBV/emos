package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.user.*;
import com.example.emos.db.pojo.TbUser;
import com.example.emos.exception.EmosException;
import com.example.emos.service.ApprovalService;
import com.example.emos.service.TbUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户Web接口")
public class UserController {

    @Resource
    private TbUserService userService;

    @Resource
    private ApprovalService approvalService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @EmosLog
    public R login(@Valid @RequestBody LoginForm loginForm) {
        HashMap<String, String> param = JSONUtil.parse(loginForm).toBean(HashMap.class);
        Integer userId = userService.login(param);
        R r = R.ok().put("result", userId != null);
        if (userId != null) {
            StpUtil.login(userId);

            // 1.查询用户的权限集合
            Set<String> permissions = userService.searchUserPermissions(userId);

            // 2.获取用户的token
            String token = StpUtil.getTokenInfo().getTokenValue();
            r.put("permissions", permissions).put("token", token);
        }
        return r;
    }

    @PostMapping("/updatePassword")
    @Operation(summary = "修改密码")
    @SaCheckLogin
    @EmosLog
    public R updatePassword(@Valid @RequestBody UpdatePasswordForm updatePasswordForm) {
        HashMap<String, Object> param = JSONUtil.parse(updatePasswordForm).toBean(HashMap.class);
        param.put("userId", StpUtil.getLoginId());
        return R.ok().put("rows", userService.updatePassword(param));
    }

    @GetMapping("/logout")
    @Operation(summary = "退出登录")
    @SaCheckLogin
    public R logout() {
        StpUtil.logout();
        return R.ok();
    }

    @GetMapping("/loadUserInfo")
    @Operation(summary = "登录成功后载入用户信息")
    @SaCheckLogin
    public R loadUserInfo() {
        int userId = StpUtil.getLoginIdAsInt();
        TbUser user = userService.query().eq("id", userId).one();
        return R.ok().put("name", user.getName()).put("photo", user.getPhoto());
    }

    @GetMapping("/searchAllUser")
    @Operation(summary = "查询所有在职用户的id和名字")
    @SaCheckLogin
    public R searchAllUser() {
        List<TbUser> users = userService.query()
                .eq("`status`", 1)
                .select("id", "name").list();
        return R.ok().put("list", users);
    }

    @PostMapping("/searchUserByPage")
    @Operation(summary = "分页查询用户信息")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R searchUserByPage(@Valid @RequestBody SearchUserByPageForm searchUserByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchUserByPageForm).toBean(HashMap.class);
        int loginId = StpUtil.getLoginIdAsInt();
        List<String> roleList = StpUtil.getRoleList(loginId);
        if (roleList.contains("部门经理")) {
            param.put("userId", loginId);
        }
        return R.ok().put("page", userService.searchUserByPage(param));
    }

    @GetMapping("/searchById")
    @Operation(summary = "根据用户id查询用户信息")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestParam("userId") Integer userId) {
        if (userId == null) {
            throw new EmosException("userId不能为空");
        }
        if (userId < 1) {
            throw new EmosException("userId不能小于1");
        }
        HashMap<String, Object> map = userService.searchById(userId);
        map.put("password", new String((byte[])map.get("password")));
        return R.ok(map);
    }

    @GetMapping("/searchUserGroupByDept")
    @Operation(summary = "查询除当前用户外其他用户的信息")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    public R searchUserGroupByDept(@RequestParam("userId") Integer userId) {
        if (userId == null) {
            throw new EmosException("userId不能为空");
        }
        if (userId < 1) {
            throw new EmosException("userId不能小于1");
        }
        return R.ok().put("list", userService.searchUserGroupByDept(userId));
    }

    @GetMapping("/searchNameAndDept")
    @Operation(summary = "查找员工姓名和部门")
    @SaCheckLogin
    public R searchNameAndDept(@RequestParam("userId") Integer userId) {
        if (userId == null || userId < 1) {
            throw new EmosException("userId格式不正确");
        }
        return R.ok(userService.searchNameAndDept(userId));
    }

    @PostMapping("/insert")
    @Operation(summary = "新增用户")
    @SaCheckPermission(value = {"ROOT", "USER:INSERT"}, mode = SaMode.OR)
    @EmosLog
    public R insert(@Valid @RequestBody InsertUserForm insertUserForm) {
        TbUser tbUser = JSONUtil.parse(insertUserForm).toBean(TbUser.class);
        tbUser.setStatus(1);
        tbUser.setRole(Arrays.toString(insertUserForm.getRole()));
        return R.ok().put("rows", userService.insert(tbUser));
    }

    @PostMapping("/update")
    @Operation(summary = "修改用户信息")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    @EmosLog
    public R update(@Valid @RequestBody UpdateUserForm updateUserForm) {
        // 判断当前要修改的用户是否是root用户
        int loginUserId = StpUtil.getLoginIdAsInt();
        Integer updateUserId = updateUserForm.getId();
        TbUser user = userService.query().select("root").eq("id", updateUserId).one();
        if (user.getRoot() == 1 && loginUserId != updateUserId) {
            return R.error("您不能修改root用户信息！");
        }
        TbUser tbUser = JSONUtil.parse(updateUserForm).toBean(TbUser.class);
        tbUser.setRole(Arrays.toString(updateUserForm.getRole()));
        return R.ok().put("rows", userService.update(tbUser));
    }

    @PostMapping("/dismiss")
    @Operation(summary = "员工离职")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    @EmosLog
    public R dismiss(@Valid @RequestBody DismissForm dismissForm) {
        Integer userId = dismissForm.getUserId();
        Integer assigneeId = dismissForm.getAssigneeId();
        if (userId.equals(assigneeId)) {
            throw new EmosException("userId和assignId不能相等");
        }
        return R.ok().put("rows", userService.dismiss(userId, assigneeId));
    }

    @PostMapping("/deleteUserByIds")
    @Operation(summary = "删除用户信息")
    @SaCheckPermission(value = {"ROOT", "USER:DELETE"}, mode = SaMode.OR)
    @EmosLog
    public R deleteUserByIds(@Valid @RequestBody DeleteUserByIdsForm deleteUserByIdsForm) {
        Integer[] ids = deleteUserByIdsForm.getIds();
        int userId = StpUtil.getLoginIdAsInt();
        if (ArrayUtil.contains(ids, userId)) {
            return R.error("您不能删除自己的帐户！");
        }
        TbUser root = userService.query().eq("root", 1).select("id").one();
        if (ArrayUtil.contains(ids, root.getId())) {
            return R.error("您不能删除root用户！");
        }
        int rows = userService.deleteUserByIds(ids);
        if (rows > 0) {
            //把被删除的用户踢下线
            for (Integer id : ids) {
                StpUtil.logout(id);
            }
        }
        return R.ok().put("rows", rows);
    }

    @GetMapping("/searchApprovalTaskCountWithMe")
    @Operation(summary = "查询当前用户的审批任务数目")
    @SaCheckLogin
    public R searchApprovalTaskCountWithMe() {
        return R.ok().put("approvalTaskCount", approvalService.searchApprovalTaskCount(StpUtil.getLoginIdAsInt()));
    }
}