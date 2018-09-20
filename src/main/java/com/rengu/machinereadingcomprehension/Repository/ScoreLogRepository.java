package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.ScoreLogEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: MachineReadingComprehension
 * @author: hanchangming
 * @create: 2018-07-09 13:01
 **/

@Repository
public interface ScoreLogRepository extends JpaRepository<ScoreLogEntity, String> {

    List<ScoreLogEntity> findByUserEntityId(Sort sort, String userId);

    List<ScoreLogEntity> findByUserEntityIdAndType(Sort sort, String userId, int type);

    boolean existsByUserEntityAndType(UserEntity userEntity, int type);
}
