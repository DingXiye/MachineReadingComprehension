package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.ScoreLogEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Repository.ScoreLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public Map<String, Double> saveScoreLog(MultipartFile multipartFile, UserEntity userEntity) {
        ScoreLogEntity scoreLogEntity = new ScoreLogEntity();
        scoreLogEntity.setUserEntity(userEntity);
        // 产生随机数据
        scoreLogEntity.setBLEU_4_Score(Math.random() * 100);
        scoreLogEntity.setROUGE_Score(Math.random() * 100);
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
