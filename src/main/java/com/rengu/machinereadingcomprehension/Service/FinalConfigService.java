package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.FinalConfigEntity;
import com.rengu.machinereadingcomprehension.Entity.ScoreLogEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Repository.FinalConfigRepository;
import com.rengu.machinereadingcomprehension.Repository.ScoreLogRepository;
import com.rengu.machinereadingcomprehension.Repository.UserRepository;
import com.rengu.machinereadingcomprehension.Utils.ApplicationConfig;
import com.rengu.machinereadingcomprehension.Utils.MachineReadingComprehensionApplicationMessage;
import com.rengu.machinereadingcomprehension.Utils.Metric;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-11 13:16
 **/

@Service
@Transactional
public class FinalConfigService {

    private final FinalConfigRepository finalConfigRepository;
    private final UserRepository userRepository;
    private final ScoreLogRepository scoreLogRepository;

    @Autowired
    public FinalConfigService(FinalConfigRepository finalConfigRepository, UserRepository userRepository, ScoreLogRepository scoreLogRepository) {
        this.finalConfigRepository = finalConfigRepository;
        this.userRepository = userRepository;
        this.scoreLogRepository = scoreLogRepository;
    }

    public FinalConfigEntity finalConfigInit() {
        FinalConfigEntity finalConfigEntity = new FinalConfigEntity();
        finalConfigEntity.setId("1");
        return finalConfigRepository.save(finalConfigEntity);
    }

    public boolean hasConfig() {
        return finalConfigRepository.existsById("1");
    }

    public FinalConfigEntity startFinal(int type) {
        FinalConfigEntity finalConfigEntity = finalConfigRepository.findById("1").get();
        switch (type) {
            case 1:
                finalConfigEntity.setCommitStartTime1(new Date());
                return finalConfigRepository.save(finalConfigEntity);
            case 2:
                finalConfigEntity.setCommitStartTime2(new Date());
                return finalConfigRepository.save(finalConfigEntity);
            case 3:
                finalConfigEntity.setCommitStartTime3(new Date());
                return finalConfigRepository.save(finalConfigEntity);
            default:
                throw new RuntimeException("请求类型错误：" + type);
        }
    }

    public FinalConfigEntity resetFinal(int type) {
        FinalConfigEntity finalConfigEntity = finalConfigRepository.findById("1").get();
        switch (type) {
            case 1:
                finalConfigEntity.setCommitStartTime1(null);
                return finalConfigRepository.save(finalConfigEntity);
            case 2:
                finalConfigEntity.setCommitStartTime2(null);
                return finalConfigRepository.save(finalConfigEntity);
            case 3:
                finalConfigEntity.setCommitStartTime3(null);
                return finalConfigRepository.save(finalConfigEntity);
            default:
                throw new RuntimeException("请求类型错误：" + type);
        }
    }

    // 保存成绩历史
    public ScoreLogEntity saveScoreLogF(UserEntity userEntity, MultipartFile multipartFile, int type) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddHHmmss");
        // 接收文件
        File resultFile = new File(FileUtils.getUserDirectoryPath() + "/User_Result/" + userEntity.getTeamName() + "_" + simpleDateFormat.format(new Date()) + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), resultFile);
        File answerFile = null;
        switch (type) {
            case 1:
                answerFile = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath().replace("classes/", "") + "F1.json");
                break;
            case 2:
                answerFile = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath().replace("classes/", "") + "F2.json");
                break;
            case 3:
                answerFile = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath().replace("classes/", "") + "F3.json");
                break;
            default:
                throw new RuntimeException("未知的请求类型");
        }
        if (!answerFile.exists()) {
            switch (type) {
                case 1:
                    answerFile = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath().replace("file:/", "/").replace("machine-reading-comprehension-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/", "") + "F1.json");
                    break;
                case 2:
                    answerFile = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath().replace("file:/", "/").replace("machine-reading-comprehension-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/", "") + "F2.json");
                    break;
                case 3:
                    answerFile = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath().replace("file:/", "/").replace("machine-reading-comprehension-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/", "") + "F3.json");
                    break;
                default:
                    throw new RuntimeException("未知的请求类型");
            }
            if (!answerFile.exists()) {
                throw new RuntimeException("服务器文件异常，请检查服务器配置。");
            }
        }
        ConcurrentHashMap<String, Double> resultMap = Metric.getScore(answerFile, resultFile);
        ScoreLogEntity scoreLogEntity = new ScoreLogEntity();
        scoreLogEntity.setUserEntity(userEntity);
        scoreLogEntity.setType(type + 2);
        scoreLogEntity.setBLEU_4_Score(new BigDecimal(resultMap.get("bleu_score") * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        scoreLogEntity.setROUGE_Score(new BigDecimal(resultMap.get("rouge_score") * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        return scoreLogRepository.save(scoreLogEntity);
    }

    public UserEntity commitFile_1(String userId, MultipartFile ref) throws Exception {
        FinalConfigEntity finalConfigEntity = finalConfigRepository.findById("1").get();
        if (finalConfigEntity.getCommitStartTime1() == null || System.currentTimeMillis() - finalConfigEntity.getCommitStartTime1().getTime() >= 1000 * 60 * 20) {
            throw new RuntimeException("比赛时间异常：" + finalConfigEntity.getCommitStartTime1());
        }
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findById(userId).get();
        if (userEntity.getCommitDateF1() == null) {
            userEntity.setCommitTimesF1(ApplicationConfig.MAX_COMMIT_TIMES_F - 1);
        } else {
            if (DateUtils.isSameDay(userEntity.getCommitDateF1(), new Date())) {
                if (userEntity.getCommitTimesF1() == 0) {
                    throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_MAX_COMMIT_TIMES);
                }
                userEntity.setCommitTimesF1(userEntity.getCommitTimesF1() - 1);
            } else {
                userEntity.setCommitTimesF1(ApplicationConfig.MAX_COMMIT_TIMES_F - 1);
            }
        }
        // 计算逻辑
        ScoreLogEntity scoreLogEntity = saveScoreLogF(userEntity, ref, 1);
        if (scoreLogEntity.getROUGE_Score() > userEntity.getRougelScoreF1()) {
            userEntity.setRougelScoreF1(scoreLogEntity.getROUGE_Score());
            userEntity.setBleu4ScoreF1(scoreLogEntity.getBLEU_4_Score());
        }
        userEntity.setCommitDateF1(new Date());
        return userRepository.save(userEntity);
    }

    public UserEntity commitFile_2(String userId, MultipartFile ref) throws Exception {
        FinalConfigEntity finalConfigEntity = finalConfigRepository.findById("1").get();
        if (finalConfigEntity.getCommitStartTime2() == null || System.currentTimeMillis() - finalConfigEntity.getCommitStartTime2().getTime() >= 1000 * 60 * 20) {
            throw new RuntimeException("比赛时间异常：" + finalConfigEntity.getCommitStartTime2());
        }
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findById(userId).get();
        if (userEntity.getCommitDateF2() == null) {
            userEntity.setCommitTimesF2(ApplicationConfig.MAX_COMMIT_TIMES_F - 1);
        } else {
            if (DateUtils.isSameDay(userEntity.getCommitDateF2(), new Date())) {
                if (userEntity.getCommitTimesF2() == 0) {
                    throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_MAX_COMMIT_TIMES);
                }
                userEntity.setCommitTimesF2(userEntity.getCommitTimesF2() - 1);
            } else {
                userEntity.setCommitTimesF2(ApplicationConfig.MAX_COMMIT_TIMES_F - 1);
            }
        }
        // 计算逻辑
        ScoreLogEntity scoreLogEntity = saveScoreLogF(userEntity, ref, 2);
        if (scoreLogEntity.getROUGE_Score() > userEntity.getRougelScoreF2()) {
            userEntity.setRougelScoreF2(scoreLogEntity.getROUGE_Score());
            userEntity.setBleu4ScoreF2(scoreLogEntity.getBLEU_4_Score());
        }
        userEntity.setCommitDateF2(new Date());
        return userRepository.save(userEntity);
    }

    public UserEntity commitFile_3(String userId, MultipartFile ref) throws Exception {
        FinalConfigEntity finalConfigEntity = finalConfigRepository.findById("1").get();
        if (finalConfigEntity.getCommitStartTime3() == null || System.currentTimeMillis() - finalConfigEntity.getCommitStartTime3().getTime() >= 1000 * 60 * 20) {
            throw new RuntimeException("比赛时间异常：" + finalConfigEntity.getCommitStartTime3());
        }
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findById(userId).get();
        if (userEntity.getCommitDateF3() == null) {
            userEntity.setCommitTimesF3(ApplicationConfig.MAX_COMMIT_TIMES_F - 1);
        } else {
            if (DateUtils.isSameDay(userEntity.getCommitDateF3(), new Date())) {
                if (userEntity.getCommitTimesF3() == 0) {
                    throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_MAX_COMMIT_TIMES);
                }
                userEntity.setCommitTimesF3(userEntity.getCommitTimesF3() - 1);
            } else {
                userEntity.setCommitTimesF3(ApplicationConfig.MAX_COMMIT_TIMES_F - 1);
            }
        }
        // 计算逻辑
        ScoreLogEntity scoreLogEntity = saveScoreLogF(userEntity, ref, 3);
        if (scoreLogEntity.getROUGE_Score() > userEntity.getRougelScoreF3()) {
            userEntity.setRougelScoreF3(scoreLogEntity.getROUGE_Score());
            userEntity.setBleu4ScoreF3(scoreLogEntity.getBLEU_4_Score());
        }
        userEntity.setCommitDateF3(new Date());
        return userRepository.save(userEntity);
    }

    public List<UserEntity> orderUser(int type) {
        switch (type) {
            case 1:
                List<UserEntity> userEntityListF1 = userRepository.findByTeamNameNotNull(new Sort(new Sort.Order(Sort.Direction.DESC, "rougelScoreF1"), new Sort.Order(Sort.Direction.DESC, "bleu4ScoreF1"), new Sort.Order(Sort.Direction.ASC, "commitDateF1")));
                Iterator<UserEntity> userEntityIteratorF1 = userEntityListF1.iterator();
                while (userEntityIteratorF1.hasNext()) {
                    UserEntity userEntity = userEntityIteratorF1.next();
                    if (userEntity.getCommitDateF1() == null || !scoreLogRepository.existsByUserEntityAndType(userEntity, type + 2)) {
                        userEntityIteratorF1.remove();//使用迭代器的删除方法删除
                    }
                }
                for (int i = 0; i < userEntityListF1.size(); i++) {
                    UserEntity userEntity = userEntityListF1.get(i);
                    userEntity.setF1Index(i + 1);
                    userRepository.save(userEntity);
                }
                return userEntityListF1;
            case 2:
                List<UserEntity> userEntityListF2 = userRepository.findByTeamNameNotNull(new Sort(new Sort.Order(Sort.Direction.DESC, "rougelScoreF2"), new Sort.Order(Sort.Direction.DESC, "bleu4ScoreF2"), new Sort.Order(Sort.Direction.ASC, "commitDateF2")));
                Iterator<UserEntity> userEntityIteratorF2 = userEntityListF2.iterator();
                while (userEntityIteratorF2.hasNext()) {
                    UserEntity userEntity = userEntityIteratorF2.next();
                    if (userEntity.getCommitDateF2() == null || !scoreLogRepository.existsByUserEntityAndType(userEntity, type + 2)) {
                        userEntityIteratorF2.remove();//使用迭代器的删除方法删除
                    }
                }
                for (int i = 0; i < userEntityListF2.size(); i++) {
                    UserEntity userEntity = userEntityListF2.get(i);
                    userEntity.setF2Index(i + 1);
                    userRepository.save(userEntity);
                }
                return userEntityListF2;
            case 3:
                List<UserEntity> userEntityListF3 = userRepository.findByTeamNameNotNull(new Sort(new Sort.Order(Sort.Direction.DESC, "rougelScoreF3"), new Sort.Order(Sort.Direction.DESC, "bleu4ScoreF3"), new Sort.Order(Sort.Direction.ASC, "commitDateF3")));
                Iterator<UserEntity> userEntityIteratorF3 = userEntityListF3.iterator();
                while (userEntityIteratorF3.hasNext()) {
                    UserEntity userEntity = userEntityIteratorF3.next();
                    if (userEntity.getCommitDateF3() == null || !scoreLogRepository.existsByUserEntityAndType(userEntity, type + 2)) {
                        userEntityIteratorF3.remove();//使用迭代器的删除方法删除
                    }
                }
                for (int i = 0; i < userEntityListF3.size(); i++) {
                    UserEntity userEntity = userEntityListF3.get(i);
                    userEntity.setF3Index(i + 1);
                    userRepository.save(userEntity);
                }
                return userEntityListF3;
            default:
                throw new RuntimeException("未知的请求类型");
        }
    }
}
