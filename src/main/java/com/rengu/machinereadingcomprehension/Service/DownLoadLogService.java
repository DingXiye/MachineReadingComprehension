package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.DownLoadLogEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Repository.DownLoadLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-08-31 09:05
 **/

@Service
@Transactional
public class DownLoadLogService {

    private final DownLoadLogRepository downLoadLogRepository;

    @Autowired
    public DownLoadLogService(DownLoadLogRepository downLoadLogRepository) {
        this.downLoadLogRepository = downLoadLogRepository;
    }

    public DownLoadLogEntity saveDownloadLog(int type, UserEntity loginUser) {
        DownLoadLogEntity downLoadLogEntity = new DownLoadLogEntity();
        downLoadLogEntity.setType(type);
        downLoadLogEntity.setUserEntity(loginUser);
        return downLoadLogRepository.save(downLoadLogEntity);
    }

    public long countDownloadLogs() {
        return downLoadLogRepository.count();
    }

    public long countDownloadLogsByType(int type) {
        return downLoadLogRepository.countByType(type);
    }
}
