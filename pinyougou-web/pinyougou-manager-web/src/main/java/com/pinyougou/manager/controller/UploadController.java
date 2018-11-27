package com.pinyougou.manager.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-06<p>
 */
@RestController
public class UploadController {

    /** 注入属性文件中的属性名对应的属性值 */
    @Value("${fileServerUrl}")
    private String fileServerUrl;

    /** 上传文件 */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file")MultipartFile multipartFile){
        // {url:'', status : 200}
        Map<String, Object> data = new HashMap<>();
        data.put("status", 500);
        try{
            // 获取上传文件的名称
            String filename = multipartFile.getOriginalFilename();
            // 获取上传文件的字节数组
            byte[] bytes = multipartFile.getBytes();

            /** ############## 上传文件到FastDFS文件服务器 ############## */
            // 获取fastdfs-client.conf配置文件
            String confFilename = this.getClass().getResource("/fastdfs-client.conf").getPath();
            // 初始化客户端全局对象
            ClientGlobal.init(confFilename);
            // 创建存储客户端对象
            StorageClient storageClient = new StorageClient();
            // 调用文件上传的方法
            String[] arr = storageClient.upload_file(bytes, FilenameUtils.getExtension(filename), null);
            // http://192.168.12.131/ group1 / M00/00/03/xxxxx.jpg
            // 访问图片：http://192.168.12.131/group1/M00/00/03/wKgMg1vb3vWAGHNAAAMGxOgwmic712.jpg
            // [group1, M00/00/03/wKgMg1vb3vWAGHNAAAMGxOgwmic712.jpg]
            System.out.println("${fileServerUrl}: " + fileServerUrl);
            // 定义StringBuilder拼接图片的url
            StringBuilder url = new StringBuilder(fileServerUrl);
            for (String str : arr){
                url.append("/" + str);
            }
            data.put("status", 200);
            data.put("url", url.toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }
}
