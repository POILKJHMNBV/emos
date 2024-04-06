package com.example.emos.common.util.tencent.oss;

import cn.hutool.core.util.IdUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 腾讯云对象存储服务工具类
 */
@Component
@PropertySource("classpath:tencent.properties")
public class COSUtil {

    @Value("${tencent.cloud.secretId}")
    private String secretId;

    @Value("${tencent.cloud.secretKey}")
    private String secretKey;

    @Value("${tencent.cloud.region}")
    private String region;

    @Value("${tencent.cloud.bucket}")
    private String bucket;

    @Value("${tencent.cloud.archivePath}")
    private String archivePath;

    private COSClient getCosClient() {
        BasicCOSCredentials basicCOSCredentials = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(basicCOSCredentials, clientConfig);
    }

    /**
     * 将文件上传到存储桶
     * @param file 要上传的文件
     * @param typeEnum 业务类型
     * @return url：文件访问路径   path:文件在存储桶中的相对路径
     */
    public Map<String, Object> uploadFile(MultipartFile file, TypeEnum typeEnum) throws IOException {
        // 文件要存放的相对路径
        String path = null;
        String filename = file.getOriginalFilename();

        if (typeEnum == TypeEnum.ARCHIVE && filename != null) {
            path = archivePath + IdUtil.simpleUUID() + filename.substring(filename.lastIndexOf("."));
        }

        // 文件的元数据信息
        ObjectMetadata fileMetadata = new ObjectMetadata();
        fileMetadata.setContentLength(file.getSize());
        fileMetadata.setContentEncoding("UTF-8");
        fileMetadata.setContentType(file.getContentType());

        // 创建请求对象
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, file.getInputStream(), fileMetadata);
        // 指定存储类型
        putObjectRequest.setStorageClass(StorageClass.Standard);

        // 获取 COSClient 对象
        COSClient cosClient = getCosClient();

        // 上传文件
        cosClient.putObject(putObjectRequest);

        // 关闭 COSClient
        cosClient.shutdown();
        return Map.of("url", "https://" + bucket + ".cos." + region + ".myqcloud.com" + path,
                      "path", path != null ? path : "");
    }

    /**
     * 删除存储桶中的文件
     * @param paths 文件在存储桶中的相对路径
     */
    public void deleteFile(String[] paths) {
        COSClient client = getCosClient();
        for (String path : paths) {
            client.deleteObject(bucket, path);
        }
        client.shutdown();
    }
}
