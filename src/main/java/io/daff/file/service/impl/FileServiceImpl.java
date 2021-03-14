package io.daff.file.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import io.daff.exception.FileUploadException;
import io.daff.exception.ParamValidateException;
import io.daff.file.service.FileService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author daffupman
 * @since 2020/8/23
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 允许上传的图片类型
     */
    protected final String[] ALLOW_UPLOAD_PICTURE_TYPES = {"jpg", "jpeg", "gif"};

    @Override
    public String upload(MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            throw new ParamValidateException("上传的文件不可为空，请重新选择");
        }

        return uploadToFastDfs(file);
    }

    private String uploadToFastDfs(MultipartFile file) throws Exception {

        if (!file.isEmpty()) {
            StorePath storePath = fastFileStorageClient.uploadFile(
                    file.getInputStream(),
                    file.getSize(),
                    extractFileExtensionName(file),
                    null
            );
            prechecked(file);
            String fastDfsUrl = "http://192.168.65.20:8888/";
            return fastDfsUrl + storePath.getFullPath();
        }

        return null;
    }

    protected String generateAvatarName(MultipartFile file) {
        return "avatar_" + System.currentTimeMillis() + "." + extractFileExtensionName(file);
    }

    /**
     * 对文件做检查：是否是允许上传的文件
     */
    protected void prechecked(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            log.error("上传的文件，名称为空");
            throw new FileUploadException("文件名不能为空");
        }

        String fileExtensionName = extractFileExtensionName(file);

        // 检查是否是允许上传的文件类型
        boolean valid = false;
        for (String allowUploadPictureType : ALLOW_UPLOAD_PICTURE_TYPES) {
            if (fileExtensionName.equalsIgnoreCase(allowUploadPictureType)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new FileUploadException("不支持的图片类型");
        }
    }

    /**
     * 提取文件的后缀名
     */
    protected String extractFileExtensionName(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            throw new IllegalArgumentException("file name is empty");
        }

        String[] originalFilenameArray = originalFilename.split("\\.");
        if (originalFilenameArray.length <= 0) {
            throw new IllegalArgumentException("file has no extension name");
        }

        return originalFilenameArray[originalFilenameArray.length - 1];
    }

}
