package com.rengu.machinereadingcomprehension.Utils;

import com.rengu.machinereadingcomprehension.Entity.RoleEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Service.RoleService;
import com.rengu.machinereadingcomprehension.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Order(value = 1)
@Configuration
public class MachineReadingComprehensionApplicationInit implements ApplicationRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public MachineReadingComprehensionApplicationInit(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 管理员角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME)) {
            RoleEntity roleArgs = new RoleEntity();
            roleArgs.setName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME);
            roleArgs.setDescription(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME);
            roleService.saveRole(roleArgs);
        }
        // 用户角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME)) {
            RoleEntity roleArgs = new RoleEntity();
            roleArgs.setName(ApplicationConfig.DEFAULT_USER_ROLE_NAME);
            roleArgs.setDescription(ApplicationConfig.DEFAULT_USER_ROLE_NAME);
            roleService.saveRole(roleArgs);
        }
        // 审核通过角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_ACCEPT_ROLE_NAME)) {
            RoleEntity roleArgs = new RoleEntity();
            roleArgs.setName(ApplicationConfig.DEFAULT_ACCEPT_ROLE_NAME);
            roleArgs.setDescription(ApplicationConfig.DEFAULT_ACCEPT_ROLE_NAME);
            roleService.saveRole(roleArgs);
        }
        // 审核未通过角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_DENIED_ROLE_NAME)) {
            RoleEntity roleArgs = new RoleEntity();
            roleArgs.setName(ApplicationConfig.DEFAULT_DENIED_ROLE_NAME);
            roleArgs.setDescription(ApplicationConfig.DEFAULT_DENIED_ROLE_NAME);
            roleService.saveRole(roleArgs);
        }
        if (!userService.hasUserByUsername(ApplicationConfig.DEFAULT_USER_USERNAME)) {
            UserEntity userArgs = new UserEntity();
            userArgs.setUsername(ApplicationConfig.DEFAULT_USER_USERNAME);
            userArgs.setPassword(ApplicationConfig.DEFAULT_USER_PASSWORD);
            userService.saveUser(userArgs, roleService.getRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME), roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME));
        }
    }
}
