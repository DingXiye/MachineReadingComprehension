package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.CrewEntity;
import com.rengu.machinereadingcomprehension.Entity.RoleEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Repository.UserRepository;
import com.rengu.machinereadingcomprehension.Utils.ApplicationConfig;
import com.rengu.machinereadingcomprehension.Utils.MachineReadingComprehensionApplicationMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import java.util.*;

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

    public boolean hasUserByTelephoneNumber(String telephoneNumber) {
        if (StringUtils.isEmpty(telephoneNumber)) {
            return false;
        }
        return userRepository.findByTelephoneNumber(telephoneNumber).isPresent();
    }

    public boolean hasUserByTeamName(String teamName) {
        if (StringUtils.isEmpty(teamName)) {
            return false;
        }
        return userRepository.findByTeamName(teamName).isPresent();
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
        if (hasUserByTeamName(userArgs.getTeamName())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_TEAM_NAME_EXISTS);
        }
        if (hasUserByTelephoneNumber(userArgs.getTelephoneNumber())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_TELEPHONENUMBER_EXISTS);
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userArgs, userEntity, "id", "createTime", "password", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "roleEntities", "crewEntities");
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userArgs.getPassword()));
        userEntity.setRoleEntities(Arrays.asList(roleEntities));
        return userRepository.save(userEntity);
    }

    public UserEntity saveUser(MultipartFile badge, String username, String password, String email, String telephoneNumber, String name, int age, int sex, String teamName, String organization) throws IOException {
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
        // 保存已上传的图片
        String badgePath = (FileUtils.getUserDirectoryPath() + "/user-badge/" + UUID.randomUUID() + "." + FilenameUtils.getExtension(badge.getOriginalFilename())).replace("\\", "/");
        FileUtils.copyToFile(badge.getInputStream(), new File(badgePath));
        userArgs.setBadgePath(badgePath);
        return saveUser(userArgs, roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME));
    }

    public UserEntity saveAdminUser(UserEntity userArgs) {
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
        // 修改电话
        if (!StringUtils.isEmpty(userArgs.getTelephoneNumber())) {
            userEntity.setTelephoneNumber(userArgs.getTelephoneNumber());
        }
        // 修改邮件地址
        if (!StringUtils.isEmpty(userArgs.getEmail())) {
            userEntity.setEmail(userArgs.getEmail());
        }
        return userRepository.save(userEntity);
    }

    public UserEntity patchUserPassword(String userId, String password) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        if (StringUtils.isEmpty(password)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_PASSWORD_PARAM_NOT_FOUND);
        }
        userEntity.setPassword(new BCryptPasswordEncoder().encode(password));
        return userRepository.save(userEntity);
    }

    public UserEntity recommit(String userId, MultipartFile badge, UserEntity userArgs) throws IOException {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        if (badge != null) {
            if (!badge.isEmpty()) {
                String badgePath = (FileUtils.getUserDirectoryPath() + "/user-badge/" + UUID.randomUUID() + "." + FilenameUtils.getExtension(badge.getOriginalFilename())).replace("\\", "/");
                FileUtils.copyToFile(badge.getInputStream(), new File(badgePath));
                userEntity.setBadgePath(badgePath);
            }
        }
        if (!StringUtils.isEmpty(userArgs.getUsername()) && !userEntity.getUsername().equals(userArgs.getUsername())) {
            if (hasUserByUsername(userArgs.getUsername())) {
                throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_USERNAME_EXISTS);
            }
            userEntity.setUsername(userArgs.getUsername());
        }
        if (!StringUtils.isEmpty(userArgs.getPassword())) {
            userEntity.setPassword(new BCryptPasswordEncoder().encode(userArgs.getPassword()));
        }
        if (!StringUtils.isEmpty(userArgs.getEmail())) {
            userEntity.setEmail(userArgs.getEmail());
        }
        if (!StringUtils.isEmpty(userArgs.getTelephoneNumber()) && !userEntity.getTelephoneNumber().equals(userArgs.getTelephoneNumber())) {
            if (hasUserByTelephoneNumber(userArgs.getTelephoneNumber())) {
                throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_TELEPHONENUMBER_EXISTS);
            }
            userEntity.setTelephoneNumber(userArgs.getTelephoneNumber());
        }
        if (!StringUtils.isEmpty(userArgs.getName())) {
            userEntity.setName(userArgs.getName());
        }
        userEntity.setAge(userArgs.getAge());
        userEntity.setSex(userArgs.getSex());
        if (!StringUtils.isEmpty(userArgs.getTeamName()) && !userEntity.getTeamName().equals(userArgs.getTeamName())) {
            if (hasUserByTeamName(userArgs.getTeamName())) {
                throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_TEAM_NAME_EXISTS);
            }
            userEntity.setTeamName(userArgs.getTeamName());
        }
        if (!StringUtils.isEmpty(userArgs.getOrganization())) {
            userEntity.setOrganization(userArgs.getOrganization());
        }
        List<RoleEntity> roleList = new ArrayList<>();
        roleList.add(roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME));
        userEntity.setRoleEntities(roleList);
        userEntity.setMessage(null);
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

    public UserEntity patchUserAccept(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        Set<RoleEntity> roleEntitySet = new HashSet<>(userEntity.getRoleEntities());
        roleEntitySet.remove(roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME));
        roleEntitySet.add(roleService.getRoleByName(ApplicationConfig.DEFAULT_ACCEPT_ROLE_NAME));
        userEntity.setRoleEntities(new ArrayList<>(roleEntitySet));
        return userRepository.save(userEntity);
    }

    public UserEntity patchUserDenied(String userId, String message) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        Set<RoleEntity> roleEntitySet = new HashSet<>(userEntity.getRoleEntities());
        roleEntitySet.remove(roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME));
        roleEntitySet.add(roleService.getRoleByName(ApplicationConfig.DEFAULT_DENIED_ROLE_NAME));
        userEntity.setRoleEntities(new ArrayList<>(roleEntitySet));
        userEntity.setMessage(message);
        return userRepository.save(userEntity);
    }

    public List<UserEntity> getUserByRoleName(String rolename) {
        List<UserEntity> userEntityList = userRepository.findAll();
        List<UserEntity> resultUserEntityList = new ArrayList<>();
        for (UserEntity userEntity : userEntityList) {
            if (userEntity.getRoleEntities().contains(roleService.getRoleByName(rolename)) && !userEntity.getRoleEntities().contains(roleService.getRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME))) {
                resultUserEntityList.add(userEntity);
            }
        }
        return resultUserEntityList;
    }

    public UserEntity commitFile(String userId, MultipartFile multipartFile) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        if (userEntity.getCommitDate() == null) {
            userEntity.setCommitDate(new Date());
            userEntity.setCommitTimes(ApplicationConfig.MAX_COMMIT_TIMES);
        } else {
            if (DateUtils.isSameDay(userEntity.getCommitDate(), new Date())) {
                if (userEntity.getCommitTimes() == 0) {
                    throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_MAX_COMMIT_TIMES);
                }
                userEntity.setCommitTimes(userEntity.getCommitTimes() - 1);
                userEntity.setCommitDate(new Date());
            } else {
                userEntity.setCommitDate(new Date());
                userEntity.setCommitTimes(ApplicationConfig.MAX_COMMIT_TIMES);
            }
        }
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

    public CrewEntity patchCrew(String userId, String crewId, CrewEntity crewArgs) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        return crewService.patchCrew(userEntity, crewId, crewArgs);
    }

    public CrewEntity getCrewById(String userId, String crewId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        return crewService.getCrewById(crewId);
    }

    public List<CrewEntity> getCrewByUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        return crewService.getCrewByUserId(userId);
    }
}
