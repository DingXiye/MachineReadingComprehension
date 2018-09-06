package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.ScoreLogEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Repository.ScoreLogRepository;
import com.rengu.machinereadingcomprehension.Utils.AESUtils;
import com.rengu.machinereadingcomprehension.Utils.ApplicationConfig;
import com.rengu.machinereadingcomprehension.Utils.Metric;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: MachineReadingComprehension
 * @author: hanchangming
 * @create: 2018-07-09 13:03
 **/

@Service
@Transactional
public class ScoreLogService {

    private final ScoreLogRepository scoreLogRepository;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddHHmmss");

    @Autowired
    public ScoreLogService(ScoreLogRepository scoreLogRepository) {
        this.scoreLogRepository = scoreLogRepository;
    }

    // 保存成绩历史
    public ScoreLogEntity saveScoreLog(UserEntity userEntity, MultipartFile multipartFile, int type) throws Exception {
        // 接收文件
        File resultFile = new File(FileUtils.getUserDirectoryPath() + "/User_Result/" + userEntity.getTeamName() + "_" + simpleDateFormat.format(new Date()) + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), resultFile);
        File answerFile = ApplicationConfig.answerFile;
        if (answerFile == null) {
            File encryptFile = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath().replace("classes/", "") + "encrypt.json");
            if (!encryptFile.exists()) {
                encryptFile = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath().replace("file:/", "/").replace("machine-reading-comprehension-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/", "") + "encrypt.json");
                if (!encryptFile.exists()) {
                    throw new RuntimeException("服务器文件异常，请检查服务器配置。");
                }
            }
            ApplicationConfig.answerFile = AESUtils.decrypt(encryptFile, ApplicationConfig.ENCRYPT_AES_KEY, ApplicationConfig.RSA_PUBLIC_KEY);
            answerFile = ApplicationConfig.answerFile;
        }
        ConcurrentHashMap<String, Double> resultMap = Metric.getScore(answerFile, resultFile);
        ScoreLogEntity scoreLogEntity = new ScoreLogEntity();
        scoreLogEntity.setUserEntity(userEntity);
        scoreLogEntity.setType(type);
        scoreLogEntity.setBLEU_4_Score(resultMap.get("bleu_score") * 100);
        scoreLogEntity.setROUGE_Score(resultMap.get("rouge_score") * 100);
        return scoreLogRepository.save(scoreLogEntity);
    }

    public List<ScoreLogEntity> getScoreLogByUserAndType(UserEntity userEntity, int Type) {
        return scoreLogRepository.findByUserEntityIdAndType(new Sort(Sort.Direction.DESC, "createTime"), userEntity.getId(), Type);
    }

    public List<ScoreLogEntity> getScoreLogByUser(UserEntity userEntity) {
        return scoreLogRepository.findByUserEntityId(new Sort(Sort.Direction.DESC, "createTime"), userEntity.getId());
    }
}
