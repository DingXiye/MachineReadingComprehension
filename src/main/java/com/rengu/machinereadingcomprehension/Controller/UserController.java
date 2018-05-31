package com.rengu.machinereadingcomprehension.Controller;

import com.rengu.machinereadingcomprehension.Entity.CrewEntity;
import com.rengu.machinereadingcomprehension.Entity.ResultEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Service.ResultService;
import com.rengu.machinereadingcomprehension.Service.UserService;
import com.rengu.machinereadingcomprehension.Utils.MachineReadingComprehensionApplicationMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final ResourceLoader resourceLoader;

    @Autowired
    public UserController(UserService userService, ResourceLoader resourceLoader) {
        this.userService = userService;
        this.resourceLoader = resourceLoader;
    }

    // 建立普通用户
    @PostMapping
    public ResultEntity saveUser(@RequestParam(value = "badge") MultipartFile badge, @RequestParam(value = "username") String username, @RequestParam(value = "password") String password, @RequestParam(value = "email") String email, @RequestParam(value = "telephoneNumber") String telephoneNumber, @RequestParam(value = "name") String name, @RequestParam(value = "age") int age, @RequestParam(value = "sex") int sex, @RequestParam(value = "teamName") String teamName, @RequestParam(value = "organization") String organization, @RequestParam(value = "job") String job) throws IOException {
        return ResultService.resultBuilder(userService.saveUser(badge, username, password, email, telephoneNumber, name, age, sex, teamName, organization, job));
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

    // 查看所有用户
    @PreAuthorize(value = "hasRole('admin')")
    @GetMapping(value = "/byenabled")
    public ResultEntity getUserByEnabled(@RequestParam(value = "enabled") boolean enabled) {
        return ResultService.resultBuilder(userService.getUserByEnabled(enabled));
    }

    // 查看用户证件
    @GetMapping(value = "/{userId}/badge")
    public void getUserBadge(HttpServletResponse httpServletResponse, @PathVariable(value = "userId") String userId) throws IOException {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = userService.getUserById(userId);
        httpServletResponse.reset();
        httpServletResponse.setContentType("image/*");
        // 文件流输出
        IOUtils.copy(new FileInputStream(userEntity.getBadgePath()), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
//        return ResponseEntity.ok(resourceLoader.getResource("file:" + userEntity.getBadgePath()));
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

    @GetMapping(value = "/{userId}/crew")
    public ResultEntity getCrewByUserId(@PathVariable(value = "userId") String userId) {
        return ResultService.resultBuilder(userService.getCrewByUserId(userId));
    }

    @GetMapping(value = "/login")
    public ResultEntity userLogin(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultService.resultBuilder(loginUser);
    }
}
