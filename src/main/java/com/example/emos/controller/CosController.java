package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.example.emos.common.util.R;
import com.example.emos.common.util.tencent.oss.COSUtil;
import com.example.emos.common.util.tencent.oss.TypeEnum;
import com.example.emos.controller.form.tencent.DeleteCosFileForm;
import com.example.emos.exception.EmosException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/cos")
@Slf4j
@Tag(name = "CosController", description = "对象存储Web接口")
public class CosController {

    @Resource
    private COSUtil cosUtil;

    @PostMapping("/uploadCosFile")
    @SaCheckPermission(value = {"ROOT", "FILE:ARCHIVE"}, mode = SaMode.OR)
    @Operation(summary = "上传文件")
    public R uploadCosFile(@Param("file") MultipartFile file, @Param("type") String type) {
        TypeEnum typeEnum = TypeEnum.findByKey(type);
        if (typeEnum == null) {
            throw new EmosException("type类型错误");
        }
        try {
            Map<String, Object> map = cosUtil.uploadFile(file, typeEnum);
            return R.ok(map);
        } catch (IOException e) {
            log.error("文件"  + file.getOriginalFilename() + "上传腾讯云存储错误", e);
            throw new EmosException("文件上传出错");
        }
    }

    @PostMapping("/deleteCosFile")
    @SaCheckPermission(value = {"ROOT", "FILE:ARCHIVE"}, mode = SaMode.OR)
    @Operation(summary = "删除文件")
    public R deleteCosFile(@Valid @RequestBody DeleteCosFileForm deleteCosFileForm) {
        cosUtil.deleteFile(deleteCosFileForm.getPaths());
        return R.ok();
    }
}
