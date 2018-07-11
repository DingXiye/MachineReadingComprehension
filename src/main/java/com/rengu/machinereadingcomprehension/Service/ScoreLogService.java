package com.rengu.machinereadingcomprehension.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rengu.machinereadingcomprehension.Entity.ScoreLogEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Repository.ScoreLogRepository;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: MachineReadingComprehension
 * @author: hanchangming
 * @create: 2018-07-09 13:03
 **/

@Service
@Transactional
public class ScoreLogService {

    private final ScoreLogRepository scoreLogRepository;

    @Autowired
    public ScoreLogService(ScoreLogRepository scoreLogRepository) {
        this.scoreLogRepository = scoreLogRepository;
    }

    // 保存成绩历史
    public Map<String, Double> saveScoreLog(MultipartFile ref, MultipartFile pred, UserEntity userEntity) throws IOException {
        // 接收文件
        File refFile = new File(FileUtils.getTempDirectoryPath() + ref.getOriginalFilename());
        FileUtils.copyInputStreamToFile(ref.getInputStream(), refFile);
        File predFile = new File(FileUtils.getTempDirectoryPath() + pred.getOriginalFilename());
        FileUtils.copyInputStreamToFile(pred.getInputStream(), predFile);
        // 获取成绩
        OkHttpClient okHttpClient = new OkHttpClient();

//        RequestBody refBody = RequestBody.create(MediaType.parse("application/octet-stream"), refFile);
//        RequestBody predBody = RequestBody.create(MediaType.parse("application/octet-stream"), predFile);
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("application/octet-stream", ref.getOriginalFilename(), refBody)
//                .addFormDataPart("application/octet-stream", pred.getOriginalFilename(), predBody)
//                .build();
//        Request request = new Request.Builder()
//                .url("http://47.96.153.138:8083/test")
//                .addHeader("content-type", "multipart/form-data")
//                .post(requestBody)
//                .build();

//        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
//        RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"pred_result\"; filename=\"C:\\Users\\hanch\\Downloads\\my_pred.json\"\r\nContent-Type: application/json\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"ref_result\"; filename=\"C:\\Users\\hanch\\Downloads\\my_ref.json\"\r\nContent-Type: application/json\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
//        Request request = new Request.Builder()
//                .url("http://47.96.153.138:8083/test")
//                .post(body)
//                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
//                .addHeader("Cache-Control", "no-cache")
//                .build();

        RequestBody refBody = RequestBody.create(MediaType.parse("multipart/form-data"), refFile);
        RequestBody predBody = RequestBody.create(MediaType.parse("multipart/form-data"), predFile);
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("ref_result", ref.getOriginalFilename(), refBody)
                .addFormDataPart("pred_result", pred.getOriginalFilename(), predBody)
                .build();
        Request request = new Request.Builder()
                .url("http://47.96.153.138:8083/test")
                .addHeader("content-type", "multipart/form-data")
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        // 解析结果
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body().string());
        ScoreLogEntity scoreLogEntity = new ScoreLogEntity();
        scoreLogEntity.setUserEntity(userEntity);
        // 产生随机数据
        scoreLogEntity.setBLEU_4_Score(jsonNode.path("BLEU-4").asDouble());
        scoreLogEntity.setROUGE_Score(jsonNode.path("ROUGE-L").asDouble());
        Map<String, Double> resultMap = new HashMap<>();
        resultMap.put("BLEU_4", scoreLogEntity.getBLEU_4_Score());
        resultMap.put("ROUGE", scoreLogEntity.getROUGE_Score());
        scoreLogRepository.save(scoreLogEntity);
        return resultMap;
    }

    public List<ScoreLogEntity> getScoreLogByUser(UserEntity userEntity) {
        return scoreLogRepository.findByUserEntityId(userEntity.getId());
    }
}
