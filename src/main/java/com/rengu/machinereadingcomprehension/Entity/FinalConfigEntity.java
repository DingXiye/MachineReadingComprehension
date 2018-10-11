package com.rengu.machinereadingcomprehension.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-11 13:14
 **/

@Entity
public class FinalConfigEntity implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date commitStartTime1;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date commitStartTime2;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date commitStartTime3;

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

    public Date getCommitStartTime1() {
        return commitStartTime1;
    }

    public void setCommitStartTime1(Date commitStartTime1) {
        this.commitStartTime1 = commitStartTime1;
    }

    public Date getCommitStartTime2() {
        return commitStartTime2;
    }

    public void setCommitStartTime2(Date commitStartTime2) {
        this.commitStartTime2 = commitStartTime2;
    }

    public Date getCommitStartTime3() {
        return commitStartTime3;
    }

    public void setCommitStartTime3(Date commitStartTime3) {
        this.commitStartTime3 = commitStartTime3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinalConfigEntity that = (FinalConfigEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(commitStartTime1, that.commitStartTime1) &&
                Objects.equals(commitStartTime2, that.commitStartTime2) &&
                Objects.equals(commitStartTime3, that.commitStartTime3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createTime, commitStartTime1, commitStartTime2, commitStartTime3);
    }
}
