package com.rengu.machinereadingcomprehension.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-12 13:01
 **/

@Entity
public class DocumentEntity implements Serializable {

    @Id
    private int id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String title;
    @Column(length = 409600)
    private String content;
    private String type;
    @OneToMany(cascade = CascadeType.ALL)
    private List<MarkEntity> markEntities;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MarkEntity> getMarkEntities() {
        return markEntities;
    }

    public void setMarkEntities(List<MarkEntity> markEntities) {
        this.markEntities = markEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentEntity that = (DocumentEntity) o;
        return id == that.id &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(type, that.type) &&
                Objects.equals(markEntities, that.markEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createTime, title, content, type, markEntities);
    }
}
