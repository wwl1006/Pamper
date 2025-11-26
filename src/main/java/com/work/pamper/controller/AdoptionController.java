package com.work.pamper.controller;

import com.work.pamper.annotation.AutoControlLog;
import com.work.pamper.entity.Adoption;
import com.work.pamper.entity.AdoptionApplication;
import com.work.pamper.service.AdoptionService;
import com.work.pamper.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/adoption")
@ResponseBody
@AutoControlLog(model = "领养模块")
public class AdoptionController {
    @Autowired
    AdoptionService adoptionService;

    // 创建领养信息
    @RequestMapping(path = "/create", name = "创建领养信息接口", method = RequestMethod.POST)
    public Object createAdoption(@RequestHeader("Authorization") String token, @RequestBody Adoption adoption) {
        return adoptionService.createAdoption(token, adoption);
    }

    // 获取领养详情
    @RequestMapping(path = "/{id:\\d+}", name = "获取领养详情接口", method = RequestMethod.GET)
    public Object getAdoptionDetail(@PathVariable Long id) {
        return adoptionService.getAdoptionDetail(id);
    }

    // 获取领养列表
    @RequestMapping(path = "/list", name = "获取领养列表接口", method = RequestMethod.GET)
    public Object getAdoptionList(@RequestParam(required = false) String petType,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "12") Integer pageSize) {
        return adoptionService.getAdoptionList(petType, page, pageSize);
    }

    // 获取我发布的领养列表
    @RequestMapping(path = "/my", name = "获取我的领养接口", method = RequestMethod.GET)
    public Object getMyAdoptions(@RequestHeader("Authorization") String token,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return adoptionService.getMyAdoptions(token, page, pageSize);
    }

    // 更新领养状态
    @RequestMapping(path = "/{id}:\\d+/status", name = "更新领养状态接口", method = RequestMethod.PUT)
    public Object updateAdoptionStatus(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id,
                                       @RequestParam Integer status) {
        return adoptionService.updateAdoptionStatus(token, id, status);
    }

    // 删除领养信息
    @RequestMapping(path = "/{id:\\d+}", name = "删除领养信息接口", method = RequestMethod.DELETE)
    public Object deleteAdoption(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return adoptionService.deleteAdoption(token, id);
    }

    // 申请领养
    @RequestMapping(path = "/apply", name = "申请领养接口", method = RequestMethod.POST)
    public Object applyAdoption(@RequestHeader("Authorization") String token, @RequestBody AdoptionApplication application) {
        return adoptionService.applyAdoption(token, application);
    }

    // 获取领养信息的申请列表（发布者查看）
    @RequestMapping(path = "/{id:\\d+}/applications", name = "获取申请列表接口", method = RequestMethod.GET)
    public Object getApplicationsByAdoption(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return adoptionService.getApplicationsByAdoption(token, id);
    }

    // 获取我的申请列表
    @RequestMapping(path = "/applications/my", name = "获取我的申请接口", method = RequestMethod.GET)
    public Object getMyApplications(@RequestHeader("Authorization") String token,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return adoptionService.getMyApplications(token, page, pageSize);
    }

    // 审核申请（发布者操作）
    @RequestMapping(path = "/applications/{id:\\d+}/review", name = "审核申请接口", method = RequestMethod.PUT)
    public Object reviewApplication(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id,
                                    @RequestParam Integer status) {
        return adoptionService.reviewApplication(token, id, status);
    }

    // 上传领养宠物图片
    @RequestMapping(path = "/upload/images", name = "上传领养图片接口", method = RequestMethod.POST)
    public Object uploadImages(@RequestHeader("Authorization") String token,
                              @RequestParam("files") MultipartFile[] files) {
        return FileUtils.uploadAdoptionImages(files, token);
    }

    // 获取领养宠物图片
    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        return FileUtils.showAdoptionImage(filename);
    }
}
