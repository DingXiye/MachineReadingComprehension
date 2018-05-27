package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.CrewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewRepository extends JpaRepository<CrewEntity, String> {
}
