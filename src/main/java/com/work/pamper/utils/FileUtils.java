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

    // 帖子图片上传（支持多图）
    public static Object uploadPostImages(MultipartFile[] files, String token) {
        if (token == null || token.isEmpty()) {
            return ResultUtil.error("上传失败，缺少token");
        }

        String userId = JwtUtils.getSubject(token);
        if (files == null || files.length == 0) {
            return ResultUtil.error("上传失败，文件为空");
        }

        // 限制最多9张图片
        if (files.length > 9) {
            return ResultUtil.error("最多只能上传9张图片");
        }

        // 构建目录
        String currentDirectory = System.getProperty("user.dir");
        String postsPath = currentDirectory + File.separator + "posts" + File.separator + "images";

        File directory = new File(postsPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        java.util.List<String> uploadedUrls = new java.util.ArrayList<>();
        long timestamp = System.currentTimeMillis();

        try {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];

                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResultUtil.error("只能上传图片文件");
                }

                // 验证文件大小（10MB）
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResultUtil.error("图片大小不能超过10MB");
                }

                // 获取原始扩展名
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // 文件名：timestamp-index.ext
                String fileName = timestamp + "-" + i + extension;
                File dest = new File(postsPath, fileName);

                file.transferTo(dest);
                uploadedUrls.add("post/image/" + fileName);
            }

            return ResultUtil.success("上传成功", uploadedUrls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error("上传失败，发生异常：" + e.getMessage());
        }
    }

    // 帖子视频上传
    public static Object uploadPostVideo(MultipartFile file, String token) {
        if (token == null || token.isEmpty()) {
            return ResultUtil.error("上传失败，缺少token");
        }

        String userId = JwtUtils.getSubject(token);
        if (file.isEmpty()) {
            return ResultUtil.error("上传失败，文件为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            return ResultUtil.error("只能上传视频文件");
        }

        // 验证文件大小（100MB）
        if (file.getSize() > 100 * 1024 * 1024) {
            return ResultUtil.error("视频大小不能超过100MB");
        }

        // 构建目录
        String currentDirectory = System.getProperty("user.dir");
        String postsPath = currentDirectory + File.separator + "posts" + File.separator + "videos";

        File directory = new File(postsPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // 获取原始扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 文件名：timestamp.ext
            long timestamp = System.currentTimeMillis();
            String fileName = timestamp + extension;
            File dest = new File(postsPath, fileName);

            file.transferTo(dest);

            return ResultUtil.success("上传成功", "post/video/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error("上传失败，发生异常：" + e.getMessage());
        }
    }

    // 获取帖子图片
    public static ResponseEntity<byte[]> showPostImage(String filename) {
        return showFile("posts" + File.separator + "images", filename);
    }

    // 获取帖子视频
    public static ResponseEntity<byte[]> showPostVideo(String filename) {
        return showFile("posts" + File.separator + "videos", filename);
    }

    // 领养宠物图片上传（支持多图，最多6张）
    public static Object uploadAdoptionImages(MultipartFile[] files, String token) {
        if (token == null || token.isEmpty()) {
            return ResultUtil.error("上传失败，缺少token");
        }

        String userId = JwtUtils.getSubject(token);
        if (files == null || files.length == 0) {
            return ResultUtil.error("上传失败，文件为空");
        }

        // 限制最多6张图片
        if (files.length > 6) {
            return ResultUtil.error("最多只能上传6张图片");
        }

        // 构建目录
        String currentDirectory = System.getProperty("user.dir");
        String adoptionPath = currentDirectory + File.separator + "adoption" + File.separator + "images";

        File directory = new File(adoptionPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        java.util.List<String> uploadedUrls = new java.util.ArrayList<>();
        long timestamp = System.currentTimeMillis();

        try {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];

                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResultUtil.error("只能上传图片文件");
                }

                // 验证文件大小（10MB）
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResultUtil.error("图片大小不能超过10MB");
                }

                // 获取原始扩展名
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // 文件名：timestamp-index.ext
                String fileName = timestamp + "-" + i + extension;
                File dest = new File(adoptionPath, fileName);

                file.transferTo(dest);
                uploadedUrls.add("adoption/image/" + fileName);
            }

            return ResultUtil.success("上传成功", uploadedUrls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error("上传失败，发生异常：" + e.getMessage());
        }
    }

    // 获取领养宠物图片
    public static ResponseEntity<byte[]> showAdoptionImage(String filename) {
        return showFile("adoption" + File.separator + "images", filename);
    }

    // 宠物档案头像上传
    public static Object uploadPetAvatar(MultipartFile file, String token) {
        if (token == null || token.isEmpty()) {
            return ResultUtil.error("上传失败，缺少token");
        }

        String userId = JwtUtils.getSubject(token);
        if (file.isEmpty()) {
            return ResultUtil.error("上传失败，文件为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResultUtil.error("只能上传图片文件");
        }

        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResultUtil.error("图片大小不能超过5MB");
        }

        // 构建目录
        String currentDirectory = System.getProperty("user.dir");
        String petAvatarPath = currentDirectory + File.separator + "pets" + File.separator + "avatars";

        File directory = new File(petAvatarPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // 获取原始扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 文件名：timestamp.ext
            long timestamp = System.currentTimeMillis();
            String fileName = timestamp + extension;
            File dest = new File(petAvatarPath, fileName);

            file.transferTo(dest);

            return ResultUtil.success("上传成功", "pet/avatar/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error("上传失败，发生异常：" + e.getMessage());
        }
    }

    // 活动封面上传
    public static Object uploadActivityCover(MultipartFile file, String token) {
        if (token == null || token.isEmpty()) {
            return ResultUtil.error("上传失败，缺少token");
        }

        if (file.isEmpty()) {
            return ResultUtil.error("上传失败，文件为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResultUtil.error("只能上传图片文件");
        }

        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResultUtil.error("图片大小不能超过5MB");
        }

        // 构建目录
        String currentDirectory = System.getProperty("user.dir");
        String activityCoverPath = currentDirectory + File.separator + "activities" + File.separator + "covers";

        File directory = new File(activityCoverPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // 获取原始扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.lastIndexOf(".") > 0 ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";

            // 文件名：timestamp.ext
            long timestamp = System.currentTimeMillis();
            String fileName = timestamp + extension;
            File dest = new File(activityCoverPath, fileName);

            file.transferTo(dest);

            return ResultUtil.success("上传成功", "activity/cover/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error("上传失败，发生异常：" + e.getMessage());
        }
    }

    // 获取宠物头像
    public static ResponseEntity<byte[]> showPetAvatar(String filename) {
        return showFile("pets" + File.separator + "avatars", filename);
    }

    // 获取活动封面
    public static ResponseEntity<byte[]> showActivityCover(String filename) {
        return showFile("activities" + File.separator + "covers", filename);
    }

    // 通用文件显示方法
    private static ResponseEntity<byte[]> showFile(String folder, String filename) {
        if (filename == null || filename.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String currentDirectory = System.getProperty("user.dir");
        String filePath = currentDirectory + File.separator + folder + File.separator + filename;
        File file = new File(filePath);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            Path path = file.toPath();
            byte[] bytes = Files.readAllBytes(path);
            String contentType = Files.probeContentType(path);

            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
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
