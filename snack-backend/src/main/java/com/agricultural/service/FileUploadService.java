package com.agricultural.service;

import com.agricultural.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Service
public class FileUploadService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        // 验证文件类型
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedType(fileExtension)) {
            throw new BusinessException("不支持的文件类型，只支持：" + allowedTypes);
        }

        // 按日期创建子目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String targetDir = uploadPath + dateDir + File.separator;

        // 创建目录
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成新文件名（UUID + 原扩展名）
        String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + fileExtension;
        String targetFilePath = targetDir + newFilename;

        try {
            // 保存文件
            Path path = Paths.get(targetFilePath);
            Files.write(path, file.getBytes());

            // 返回访问路径
            return "/uploads/" + dateDir + "/" + newFilename;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 验证文件类型是否允许
     */
    private boolean isAllowedType(String fileExtension) {
        String[] allowedTypeArray = allowedTypes.split(",");
        for (String type : allowedTypeArray) {
            if (type.trim().equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }
}

