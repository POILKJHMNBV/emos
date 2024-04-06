package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.dept.DeleteDeptByIdsForm;
import com.example.emos.controller.form.dept.InsertDeptForm;
import com.example.emos.controller.form.dept.SearchDeptByPageForm;
import com.example.emos.controller.form.dept.UpdateDeptForm;
import com.example.emos.db.pojo.TbDept;
import com.example.emos.service.TbDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/dept")
@Tag(name = "DeptController", description = "部门Web接口")
public class DeptController {

    @Resource
    private TbDeptService deptService;

    @GetMapping("/searchAllDept")
    @Operation(summary = "查询所有部门")
    @SaCheckLogin
    public R searchAllDept() {
        return R.ok().put("list", deptService.query().select("id", "dept_name").list());
    }

    @PostMapping("/searchDeptByPage")
    @Operation(summary = "分页查询部门信息")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public R searchDeptByPage(@Valid @RequestBody SearchDeptByPageForm searchDeptByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchDeptByPageForm).toBean(HashMap.class);
        return R.ok().put("page", deptService.searchDeptByPage(param));
    }

    @GetMapping("/searchById")
    @Operation(summary = "根据id查询某一部门的具体信息")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestParam("deptId") Integer deptId) {
        TbDept tbDept = deptService.query().eq("id", deptId).select("dept_name", "tel", "email", "`desc`").one();
        HashMap<String, Object> map = JSONUtil.parse(tbDept).toBean(HashMap.class);
        return R.ok(map);
    }

    @PostMapping("/update")
    @Operation(summary = "更新部门信息")
    @SaCheckPermission(value = {"ROOT", "DEPT:UPDATE"}, mode = SaMode.OR)
    @EmosLog
    public R update(@Valid @RequestBody UpdateDeptForm updateDeptForm) {
        TbDept tbDept = JSONUtil.parse(updateDeptForm).toBean(TbDept.class);
        return R.ok().put("rows", deptService.update(tbDept));
    }

    @PostMapping("/insert")
    @Operation(summary = "新增部门信息")
    @SaCheckPermission(value = {"ROOT", "DEPT:INSERT"}, mode = SaMode.OR)
    @EmosLog
    public R insert(@Valid @RequestBody InsertDeptForm insertDeptForm) {
        TbDept tbDept = JSONUtil.parse(insertDeptForm).toBean(TbDept.class);
        return R.ok().put("rows", deptService.insert(tbDept));
    }

    @PostMapping("/deleteDeptByIds")
    @Operation(summary = "删除部门信息")
    @SaCheckPermission(value = {"ROOT", "DEPT:DELETE"}, mode = SaMode.OR)
    @EmosLog
    public R deleteDeptByIds(@Valid @RequestBody DeleteDeptByIdsForm deleteDeptByIdsForm) {
        return R.ok().put("rows", deptService.deleteDeptByIds(deleteDeptByIdsForm.getIds()));
    }
}
