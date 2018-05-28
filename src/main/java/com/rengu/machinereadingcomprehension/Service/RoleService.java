package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.RoleEntity;
import com.rengu.machinereadingcomprehension.Repository.RoleRepository;
import com.rengu.machinereadingcomprehension.Utils.MachineReadingComprehensionApplicationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleEntity saveRole(RoleEntity roleArgs) {
        if (roleArgs == null) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.ROLE_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(roleArgs.getName())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.ROLE_NAME_PARAM_NOT_FOUND);
        }
        if (hasRoleByName(roleArgs.getName())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.ROLE_NAME_EXISTS);
        }
        RoleEntity roleEntity = new RoleEntity();
        BeanUtils.copyProperties(roleArgs, roleEntity, "id", "createTime");
        return roleRepository.save(roleEntity);
    }

    public boolean hasRoleByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return roleRepository.findByName(name).isPresent();
    }

    public RoleEntity getRoleByName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.ROLE_NAME_PARAM_NOT_FOUND);
        }
        return roleRepository.findByName(name).get();
    }
}
