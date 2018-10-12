package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-12 13:03
 **/

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer> {
}
