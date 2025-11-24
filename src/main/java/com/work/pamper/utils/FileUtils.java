package com.work.pamper.utils;

import com.work.pamper.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
@Component
public class FileUtils {
    @Autowired
    AccountMapper accountMapper;
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

    // 展示头像: 根据 token 提取 id, 查找 id.* 图片并返回二进制
    public static ResponseEntity<byte[]> showAvatar(String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String id = JwtUtils.getSubject(token);
        if (id == null || id.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String currentDirectory = System.getProperty("user.dir");
        String avatarPath = currentDirectory + File.separator + AVATAR_FOLDER_NAME;
        File directory = new File(avatarPath);
        if (!directory.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        File[] files = directory.listFiles((dir, name) ->
                name.startsWith(id + ".") &&
                        (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                                || name.endsWith(".gif") || name.endsWith(".webp")));
        if (files == null || files.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        File img = files[0];
        try {
            Path p = img.toPath();
            byte[] bytes = Files.readAllBytes(p);
            String contentType = Files.probeContentType(p);
            if (contentType == null) {
                String lower = img.getName().toLowerCase();
                if (lower.endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
                else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;
                else if (lower.endsWith(".gif")) contentType = MediaType.IMAGE_GIF_VALUE;
                else if (lower.endsWith(".webp")) contentType = "image/webp";
                else contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 通过用户ID直接获取头像
    public static ResponseEntity<byte[]> showAvatarById(String id) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String currentDirectory = System.getProperty("user.dir");
        String avatarPath = currentDirectory + File.separator + AVATAR_FOLDER_NAME;
        File directory = new File(avatarPath);
        if (!directory.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        File[] files = directory.listFiles((dir, name) ->
                name.startsWith(id + ".") &&
                        (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                                || name.endsWith(".gif") || name.endsWith(".webp")));
        if (files == null || files.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        File img = files[0];
        try {
            Path p = img.toPath();
            byte[] bytes = Files.readAllBytes(p);
            String contentType = Files.probeContentType(p);
            if (contentType == null) {
                String lower = img.getName().toLowerCase();
                if (lower.endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
                else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;
                else if (lower.endsWith(".gif")) contentType = MediaType.IMAGE_GIF_VALUE;
                else if (lower.endsWith(".webp")) contentType = "image/webp";
                else contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
