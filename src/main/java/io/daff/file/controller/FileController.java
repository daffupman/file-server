package io.daff.file.controller;

import io.daff.consts.SystemConstants;
import io.daff.entity.Response;
import io.daff.exception.FileUploadException;
import io.daff.file.anno.ApiVersion;
import io.daff.file.service.FileService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 文件 接口
 *
 * @author daffupman
 * @since 2020/8/17
 */
@RestController
@ApiVersion("v1")
@RequestMapping("/file")
@Slf4j
@Validated
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = SystemConstants.ACCESS_TOKEN, value = "访问令牌", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "file", value = "文件", required = true, paramType = "body", dataType = "file"),
    })
    @ApiResponses({
            @ApiResponse(code = 3000, message = "参数校验失败"),
            @ApiResponse(code = 4000, message = "认证失败"),
            @ApiResponse(code = 5001, message = "token错误")
    })
    @PostMapping(consumes = {"multipart/form-data"})
    public Response<String> uploadFile(@NotNull(message = "文件不可为空，请补充填写") MultipartFile file) {

        String showPath;
        try {
            showPath = fileService.upload(file);
        } catch (Exception e) {
            log.error("文件上传错误", e);
            throw new FileUploadException(e.getMessage());
        }
        return Response.ok(showPath);
    }
}
