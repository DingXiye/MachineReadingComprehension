package com.rengu.machinereadingcomprehension.Task;

import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Repository.UserRepository;
import com.rengu.machinereadingcomprehension.Utils.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: MachineReadingComprehension
 * @author: hanchangming
 * @create: 2018-07-26 09:51
 **/

@Component
public class TimerTask {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void resetTimes() {
        List<UserEntity> userEntities = userRepository.findAll();
        for (UserEntity userEntity : userEntities) {
            userEntity.setCommitTimesT(ApplicationConfig.MAX_COMMIT_TIMES_T);
            userEntity.setCommitTimesP(ApplicationConfig.MAX_COMMIT_TIMES_P);
            userEntity.setCommitTimesF(ApplicationConfig.MAX_COMMIT_TIMES_F);
        }
        userRepository.saveAll(userEntities);
    }
}
