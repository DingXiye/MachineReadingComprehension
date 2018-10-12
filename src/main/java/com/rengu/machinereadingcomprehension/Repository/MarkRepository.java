package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.MarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-12 14:00
 **/

@Repository
public interface MarkRepository extends JpaRepository<MarkEntity, String> {
}
