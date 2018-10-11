package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.FinalConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-11 13:16
 **/

@Repository
public interface FinalConfigRepository extends JpaRepository<FinalConfigEntity, String> {
}
