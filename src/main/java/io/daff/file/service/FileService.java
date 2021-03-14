package io.daff.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author daffupman
 * @since 2020/8/23
 */
public interface FileService {

    /**
     * 将指定的文件上传到tomcat
     *
     * @param file 需要上传的文件
     * @return 可以访问的路径
     */
    String upload(MultipartFile file) throws Exception;
}
