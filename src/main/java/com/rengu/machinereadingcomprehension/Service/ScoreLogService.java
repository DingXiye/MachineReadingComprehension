package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Repository.ScoreLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
