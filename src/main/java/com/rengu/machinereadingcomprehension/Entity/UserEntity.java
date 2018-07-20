package com.rengu.machinereadingcomprehension.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.*;

@Entity
public class UserEntity implements UserDetails, Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String message;
    private String username;
    private String password;
    private String email;
    private String telephoneNumber;
    private String name;
    private int age;
    private int sex;
    private String teamName;
    private String organization;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private String badgePath;
    private double bleu4ScoreT;
    private double rougelScoreT;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date commitDateT;
    private int commitTimesT;
    private double bleu4ScoreP;
    private double rougelScoreP;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date commitDateP;
    private int commitTimesP;
    private double bleu4ScoreF;
    private double rougelScoreF;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date commitDateF;
    private int commitTimesF;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<RoleEntity> roleEntities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return age == that.age &&
                sex == that.sex &&
                accountNonExpired == that.accountNonExpired &&
                accountNonLocked == that.accountNonLocked &&
                credentialsNonExpired == that.credentialsNonExpired &&
                enabled == that.enabled &&
                Double.compare(that.bleu4ScoreT, bleu4ScoreT) == 0 &&
                Double.compare(that.rougelScoreT, rougelScoreT) == 0 &&
                commitTimesT == that.commitTimesT &&
                Double.compare(that.bleu4ScoreP, bleu4ScoreP) == 0 &&
                Double.compare(that.rougelScoreP, rougelScoreP) == 0 &&
                commitTimesP == that.commitTimesP &&
                Double.compare(that.bleu4ScoreF, bleu4ScoreF) == 0 &&
                Double.compare(that.rougelScoreF, rougelScoreF) == 0 &&
                commitTimesF == that.commitTimesF &&
                Objects.equals(id, that.id) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(message, that.message) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(email, that.email) &&
                Objects.equals(telephoneNumber, that.telephoneNumber) &&
                Objects.equals(name, that.name) &&
                Objects.equals(teamName, that.teamName) &&
                Objects.equals(organization, that.organization) &&
                Objects.equals(badgePath, that.badgePath) &&
                Objects.equals(commitDateT, that.commitDateT) &&
                Objects.equals(commitDateP, that.commitDateP) &&
                Objects.equals(commitDateF, that.commitDateF) &&
                Objects.equals(roleEntities, that.roleEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createTime, message, username, password, email, telephoneNumber, name, age, sex, teamName, organization, accountNonExpired, accountNonLocked, credentialsNonExpired, enabled, badgePath, bleu4ScoreT, rougelScoreT, commitDateT, commitTimesT, bleu4ScoreP, rougelScoreP, commitDateP, commitTimesP, bleu4ScoreF, rougelScoreF, commitDateF, commitTimesF, roleEntities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        for (RoleEntity roleEntity : roleEntities) {
            grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_" + roleEntity.getName()));
        }
        return grantedAuthorityList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBadgePath() {
        return badgePath;
    }

    public void setBadgePath(String badgePath) {
        this.badgePath = badgePath;
    }

    public double getBleu4ScoreT() {
        return bleu4ScoreT;
    }

    public void setBleu4ScoreT(double bleu4ScoreT) {
        this.bleu4ScoreT = bleu4ScoreT;
    }

    public double getRougelScoreT() {
        return rougelScoreT;
    }

    public void setRougelScoreT(double rougelScoreT) {
        this.rougelScoreT = rougelScoreT;
    }

    public Date getCommitDateT() {
        return commitDateT;
    }

    public void setCommitDateT(Date commitDateT) {
        this.commitDateT = commitDateT;
    }

    public int getCommitTimesT() {
        return commitTimesT;
    }

    public void setCommitTimesT(int commitTimesT) {
        this.commitTimesT = commitTimesT;
    }

    public double getBleu4ScoreP() {
        return bleu4ScoreP;
    }

    public void setBleu4ScoreP(double bleu4ScoreP) {
        this.bleu4ScoreP = bleu4ScoreP;
    }

    public double getRougelScoreP() {
        return rougelScoreP;
    }

    public void setRougelScoreP(double rougelScoreP) {
        this.rougelScoreP = rougelScoreP;
    }

    public Date getCommitDateP() {
        return commitDateP;
    }

    public void setCommitDateP(Date commitDateP) {
        this.commitDateP = commitDateP;
    }

    public int getCommitTimesP() {
        return commitTimesP;
    }

    public void setCommitTimesP(int commitTimesP) {
        this.commitTimesP = commitTimesP;
    }

    public double getBleu4ScoreF() {
        return bleu4ScoreF;
    }

    public void setBleu4ScoreF(double bleu4ScoreF) {
        this.bleu4ScoreF = bleu4ScoreF;
    }

    public double getRougelScoreF() {
        return rougelScoreF;
    }

    public void setRougelScoreF(double rougelScoreF) {
        this.rougelScoreF = rougelScoreF;
    }

    public Date getCommitDateF() {
        return commitDateF;
    }

    public void setCommitDateF(Date commitDateF) {
        this.commitDateF = commitDateF;
    }

    public int getCommitTimesF() {
        return commitTimesF;
    }

    public void setCommitTimesF(int commitTimesF) {
        this.commitTimesF = commitTimesF;
    }

    public List<RoleEntity> getRoleEntities() {
        return roleEntities;
    }

    public void setRoleEntities(List<RoleEntity> roleEntities) {
        this.roleEntities = roleEntities;
    }
}
