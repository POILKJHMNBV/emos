package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.amecttype.DeleteAmectTypeByIdsForm;
import com.example.emos.controller.form.amecttype.InsertAmectTypeForm;
import com.example.emos.controller.form.amecttype.SearchAmectTypeByPageForm;
import com.example.emos.controller.form.amecttype.UpdateAmectTypeByIdForm;
import com.example.emos.db.pojo.TbAmect;
import com.example.emos.db.pojo.TbAmectType;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbAmectTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/amect_type")
@Tag(name = "AmectTypeController", description = "罚款类型Web接口")
@Slf4j
public class AmectTypeController {

    @Resource
    private TbAmectTypeService amectTypeService;

    @GetMapping("/searchAllAmectType")
    @Operation(summary = "查询所有的罚款类型")
    @SaCheckLogin
    public R searchAllAmectType() {
        return R.ok().put("list", amectTypeService.list());
    }

    @PostMapping("/searchAmectTypeByPage")
    @Operation(summary = "查询罚款类型分页记录")
    @SaCheckPermission(value = {"ROOT"})
    public R searchAmectTypeByPage(@Valid @RequestBody SearchAmectTypeByPageForm searchAmectTypeByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchAmectTypeByPageForm).toBean(HashMap.class);
        return R.ok().put("page", amectTypeService.searchAmectTypeByPage(param));
    }

    @GetMapping("/searchById")
    @Operation(summary = "查询具体的罚款类型信息")
    @SaCheckPermission(value = {"ROOT"})
    public R searchById(@RequestParam("amectTypeid") Integer amectTypeid) {
        if (amectTypeid == null || amectTypeid < 1) {
            throw new EmosException("罚款类型id格式不正确");
        }
        TbAmectType amectType = amectTypeService.query()
                .select("type", "money", "systemic")
                .eq("id", amectTypeid).one();
        Map<String, Object> map = Map.of("type", amectType.getType(),
                "money", amectType.getMoney(),
                "systemic", amectType.getSystemic());
        return R.ok(map);
    }

    @PostMapping("/insert")
    @Operation(summary = "添加罚款类型")
    @SaCheckPermission(value = {"ROOT"})
    @EmosLog
    public R insert(@Valid @RequestBody InsertAmectTypeForm insertAmectTypeForm) {
        HashMap<String, Object> param = JSONUtil.parse(insertAmectTypeForm).toBean(HashMap.class);
        return R.ok().put("rows", amectTypeService.insert(param));
    }

    @PostMapping("/update")
    @Operation(summary = "更新罚款类型")
    @SaCheckPermission(value = {"ROOT"})
    @EmosLog
    public R update(@Valid @RequestBody UpdateAmectTypeByIdForm updateAmectTypeByIdForm) {
        TbAmectType tbAmectType = new TbAmectType();
        tbAmectType.setId(updateAmectTypeByIdForm.getId());
        tbAmectType.setMoney(new BigDecimal(updateAmectTypeByIdForm.getMoney()));
        tbAmectType.setType(updateAmectTypeByIdForm.getType());
        amectTypeService.updateById(tbAmectType);
        return R.ok().put("rows", 1);
    }

    @PostMapping("/deleteAmectTypeByIds")
    @Operation(summary = "删除罚款类型")
    @SaCheckPermission(value = {"ROOT"})
    @EmosLog
    public R deleteAmectTypeByIds(@Valid @RequestBody DeleteAmectTypeByIdsForm deleteAmectTypeByIdsForm) {
        Integer[] ids = deleteAmectTypeByIdsForm.getIds();
        List<Integer> list = Arrays.asList(ids);
        amectTypeService.removeBatchByIds(list);
        return R.ok().put("rows", list.size());
    }
}
