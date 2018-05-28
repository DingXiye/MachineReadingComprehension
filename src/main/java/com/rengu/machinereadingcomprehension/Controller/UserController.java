package com.rengu.machinereadingcomprehension.Controller;

import com.rengu.machinereadingcomprehension.Entity.CrewEntity;
import com.rengu.machinereadingcomprehension.Entity.ResultEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Service.ResultService;
import com.rengu.machinereadingcomprehension.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 建立普通用户
    @PreAuthorize(value = "permitAll()")
    @PostMapping
    public ResultEntity saveUser(@RequestParam(value = "IDCardFront") MultipartFile IDCardFront, @RequestParam(value = "IDCardBack") MultipartFile IDCardBack, @RequestParam(value = "badge") MultipartFile badge, UserEntity userArgs) throws IOException {
        return ResultService.resultBuilder(userService.saveUser(IDCardFront, IDCardBack, badge, userArgs));
    }

    // 建立管理员用户
    @PreAuthorize(value = "hasRole('admin')")
    @PostMapping(value = "/admin")
    public ResultEntity saveAdminUser(UserEntity userArgs) {
        return ResultService.resultBuilder(userService.saveAdminUser(userArgs));
    }

    // 删除用户
    @DeleteMapping(value = "{userId}")
    public ResultEntity deleteUser(@PathVariable(value = "userId") String userId) {
        userService.deleteUser(userId);
        return ResultService.resultBuilder(userId + "删除成功");
    }

    // 修改用户
    @PatchMapping(value = "/{userId}")
    public ResultEntity patchUser(@PathVariable(value = "userId") String userId, UserEntity userArgs) {
        return ResultService.resultBuilder(userService.patchUser(userId, userArgs));
    }

    // 修改账户状态
    @PreAuthorize(value = "hasRole('admin')")
    @PatchMapping(value = "/{userId}/enable")
    public ResultEntity patchUserEnable(@PathVariable(value = "userId") String userId) {
        return ResultService.resultBuilder(userService.patchUserEnable(userId));
    }

    // 查看用户
    @GetMapping(value = "/{userId}")
    public ResultEntity getUserById(@PathVariable(value = "userId") String userId) {
        return ResultService.resultBuilder(userService.getUserById(userId));
    }

    // 查看所有用户
    @PreAuthorize(value = "hasRole('admin')")
    @GetMapping
    public ResultEntity getUser() {
        return ResultService.resultBuilder(userService.getUser());
    }

    // 保存团队成员
    @PostMapping(value = "/{userId}/crew")
    public ResultEntity saveCrew(@PathVariable(value = "userId") String userId, CrewEntity crewArgs) {
        return ResultService.resultBuilder(userService.saveCrew(userId, crewArgs));
    }

    // 删除团队成员
    @DeleteMapping(value = "/{userId}/crew/{crewId}")
    public ResultEntity deleteCrew(@PathVariable(value = "userId") String userId, @PathVariable(value = "crewId") String crewId) {
        return ResultService.resultBuilder(userService.deleteCrew(userId, crewId));
    }
}
