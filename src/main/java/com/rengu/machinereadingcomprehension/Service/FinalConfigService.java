package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.FinalConfigEntity;
import com.rengu.machinereadingcomprehension.Repository.FinalConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-11 13:16
 **/

@Service
@Transactional
public class FinalConfigService {

    private final FinalConfigRepository finalConfigRepository;

    @Autowired
    public FinalConfigService(FinalConfigRepository finalConfigRepository) {
        this.finalConfigRepository = finalConfigRepository;
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
}
