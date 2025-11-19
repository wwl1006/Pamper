package com.work.pamper.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static final String AVATAR_FOLDER_NAME = "avatars";

    public static Object uploadAvatar(MultipartFile file,String token) {
        if (token == null || token.isEmpty()) {
            return ResultUtil.error("上传失败，缺少token");
        }
        String id = JwtUtils.getSubject(token); // 验证token合法性
        if (file.isEmpty()) {
            return ResultUtil.error("上传失败，文件为空");
        }

        // 获取当前工作目录
        String currentDirectory = System.getProperty("user.dir");
        // 构建avatars目录的完整路径
        String avatarPath = currentDirectory + File.separator + AVATAR_FOLDER_NAME;

        // 检查路径是否存在，如果不存在则创建
        File directory = new File(avatarPath);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                return ResultUtil.error("上传失败，无法创建目录");
            }
        }
        // 使用用户ID作为文件名，确保唯一性
        String fileName = id + ".png";
//        String fileName = file.getOriginalFilename();
        File dest = new File(avatarPath, fileName);
        try {
            file.transferTo(dest);
            return ResultUtil.success("上传成功", AVATAR_FOLDER_NAME + "/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error("上传失败，发生异常：" + e.getMessage());
        }
    }
}
