package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.DownLoadLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-08-31 09:02
 **/

@Repository
public interface DownLoadLogRepository extends JpaRepository<DownLoadLogEntity, String> {

    long countByType(int type);
}
