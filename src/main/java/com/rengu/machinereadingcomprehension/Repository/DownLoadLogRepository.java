package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.DownLoadLogEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-08-31 09:02
 **/

@Repository
public interface DownLoadLogRepository extends JpaRepository<DownLoadLogEntity, String> {

    long countByType(int type);

    List<DownLoadLogEntity> findDistinctByUserEntity(UserEntity userEntity);
}
