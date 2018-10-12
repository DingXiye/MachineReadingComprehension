package com.rengu.machinereadingcomprehension.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-12 13:58
 **/

@Entity
public class MarkEntity {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String question;
    private String answer;
    private String type;
    @OneToMany(cascade = CascadeType.ALL)
    private List<UserMarkEntity> userMarkEntities;

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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<UserMarkEntity> getUserMarkEntities() {
        return userMarkEntities;
    }

    public void setUserMarkEntities(List<UserMarkEntity> userMarkEntities) {
        this.userMarkEntities = userMarkEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkEntity that = (MarkEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(question, that.question) &&
                Objects.equals(answer, that.answer) &&
                Objects.equals(type, that.type) &&
                Objects.equals(userMarkEntities, that.userMarkEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createTime, question, answer, type, userMarkEntities);
    }
}
