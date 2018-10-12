package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.UserMarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-12 15:07
 **/

@Repository
public interface UserMarkRepository extends JpaRepository<UserMarkEntity, String> {

    Optional<UserMarkEntity> findByMarkIdAndUserIdAndType(String markId, String userId, int type);
}
