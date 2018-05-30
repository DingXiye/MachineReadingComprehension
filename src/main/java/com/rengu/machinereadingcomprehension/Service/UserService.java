package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.CrewEntity;
import com.rengu.machinereadingcomprehension.Entity.RoleEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Repository.UserRepository;
import com.rengu.machinereadingcomprehension.Utils.ApplicationConfig;
import com.rengu.machinereadingcomprehension.Utils.MachineReadingComprehensionApplicationMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final CrewService crewService;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, CrewService crewService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.crewService = crewService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (!hasUserByUsername(s)) {
            throw new UsernameNotFoundException(MachineReadingComprehensionApplicationMessage.USER_NOT_FOUND + ":" + s);
        }
        return userRepository.findByUsername(s).get();
    }

    public boolean hasUserByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        return userRepository.findByUsername(username).isPresent();
    }

    public UserEntity saveUser(UserEntity userArgs, RoleEntity... roleEntities) {
        if (roleEntities == null || roleEntities.length == 0) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ROLE_PARAM_NOT_FOUND);
        }
        if (userArgs == null) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userArgs.getUsername())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_USERNAME_PARAM_NOT_FOUND);
        }
        if (hasUserByUsername(userArgs.getUsername())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_USERNAME_EXISTS);
        }
        if (StringUtils.isEmpty(userArgs.getPassword())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_PASSWORD_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userArgs, userEntity, "id", "createTime", "password", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "roleEntities", "crewEntities");
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userArgs.getPassword()));
        userEntity.setRoleEntities(Arrays.asList(roleEntities));
        return userRepository.save(userEntity);
    }

    public UserEntity saveUser(MultipartFile badge, String username, String password, String email, String telephoneNumber, String name, int age, int sex, String teamName, String organization, String job) throws IOException {
        if (badge == null) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_IMAGE_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(email)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_EMAIL_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(telephoneNumber)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_TELEPHONENUMBER_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_NAME_PARAM_NOT_FOUND);
        }
        if (age == 0) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_AGE_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(teamName)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_TEAMNAME_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(organization)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ORGANIZATION_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(job)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_JOB_PARAM_NOT_FOUND);
        }
        UserEntity userArgs = new UserEntity();
        userArgs.setUsername(username);
        userArgs.setPassword(password);
        userArgs.setEmail(email);
        userArgs.setTelephoneNumber(telephoneNumber);
        userArgs.setName(name);
        userArgs.setAge(age);
        userArgs.setSex(sex);
        userArgs.setTeamName(teamName);
        userArgs.setOrganization(organization);
        userArgs.setJob(job);
        userArgs.setEnabled(false);
        return saveUser(badge, userArgs);
    }

    public UserEntity saveUser(MultipartFile badge, UserEntity userArgs) throws IOException {
        if (badge == null) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_IMAGE_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userArgs.getEmail())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_EMAIL_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userArgs.getTelephoneNumber())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_TELEPHONENUMBER_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userArgs.getName())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_NAME_PARAM_NOT_FOUND);
        }
        if (userArgs.getAge() == 0) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_AGE_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userArgs.getTeamName())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_TEAMNAME_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userArgs.getOrganization())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ORGANIZATION_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userArgs.getJob())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_JOB_PARAM_NOT_FOUND);
        }
        // 保存已上传的图片
        String badgePath = (FileUtils.getUserDirectoryPath() + "/user-badge/" + UUID.randomUUID() + "." + FilenameUtils.getExtension(badge.getOriginalFilename())).replace("\\", "/");
        FileUtils.copyToFile(badge.getInputStream(), new File(badgePath));
        userArgs.setBadgePath(badgePath);
        userArgs.setEnabled(false);
        return saveUser(userArgs, roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME));
    }

    public UserEntity saveAdminUser(UserEntity userArgs) {
        userArgs.setEnabled(true);
        return saveUser(userArgs, roleService.getRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME), roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME));
    }

    public void deleteUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    public UserEntity patchUser(String userId, UserEntity userArgs) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        if (userArgs == null) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userArgs.getPassword())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_PASSWORD_PARAM_NOT_FOUND);
        }
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userArgs.getPassword()));
        return userRepository.save(userEntity);
    }

    public UserEntity getUserById(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        return userRepository.findById(userId).get();
    }

    public List<UserEntity> getUser() {
        return userRepository.findAll();
    }

    public UserEntity patchUserEnable(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        userEntity.setEnabled(!userEntity.isEnabled());
        return userRepository.save(userEntity);
    }

    public CrewEntity saveCrew(String userId, CrewEntity crewArgs) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        return crewService.saveCrew(getUserById(userId), crewArgs);
    }

    public CrewEntity deleteCrew(String userId, String crewId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        return crewService.deleteCrew(crewId);
    }

    public List<CrewEntity> getCrewByUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        return crewService.getCrewByUserId(userId);
    }
}
