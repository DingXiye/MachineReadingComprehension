package com.rengu.machinereadingcomprehension.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rengu.machinereadingcomprehension.Entity.MarkEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Entity.UserMarkEntity;
import com.rengu.machinereadingcomprehension.Repository.MarkRepository;
import com.rengu.machinereadingcomprehension.Repository.UserMarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-12 14:01
 **/

@Service
@Transactional
public class MarkService {

    private final MarkRepository markRepository;
    private final UserMarkRepository userMarkRepository;

    @Autowired
    public MarkService(MarkRepository markRepository, UserMarkRepository userMarkRepository) {
        this.markRepository = markRepository;
        this.userMarkRepository = userMarkRepository;
    }

    public List<MarkEntity> saveMarks(JsonNode jsonNode) {
        Iterator<JsonNode> rootNodeIterator = jsonNode.elements();
        List<MarkEntity> markEntityList = new ArrayList<>();
        while (rootNodeIterator.hasNext()) {
            JsonNode sonNode = rootNodeIterator.next();
            MarkEntity markEntity = new MarkEntity();
            markEntity.setId(sonNode.get("questions_id").asText());
            markEntity.setQuestion(sonNode.get("question").asText());
            markEntity.setAnswer(sonNode.get("answer").asText());
            markEntity.setType(sonNode.get("question_type").asText());
            markEntityList.add(markEntity);
        }
        return markEntityList;
    }

    @Async
    public void updateUserMarks(UserEntity userEntity, File resultFile, int type) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(resultFile);
        Iterator<JsonNode> rootNodeIterator = rootNode.elements();
        while (rootNodeIterator.hasNext()) {
            JsonNode jsonNode = rootNodeIterator.next();
            Iterator<JsonNode> questionNodeIterator = jsonNode.get("questions").elements();
            while (questionNodeIterator.hasNext()) {
                JsonNode sonNode = questionNodeIterator.next();
                String markId = sonNode.get("questions_id").asText();
                if (markRepository.existsById(markId)) {
                    MarkEntity markEntity = markRepository.findById(markId).get();
                    Optional<UserMarkEntity> userMarkEntityOptional = userMarkRepository.findByMarkIdAndUserIdAndType(markEntity.getId(), userEntity.getId(), type);
                    UserMarkEntity userMarkEntity = null;
                    if (userMarkEntityOptional.isPresent()) {
                        userMarkEntity = userMarkEntityOptional.get();
                    } else {
                        userMarkEntity = new UserMarkEntity();
                    }
                    userMarkEntity.setAnswer(sonNode.get("answer").asText());
                    userMarkEntity.setMarkId(markEntity.getId());
                    userMarkEntity.setUserId(userEntity.getId());
                    userMarkEntity.setUsername(userEntity.getUsername());
                    userMarkEntity.setTeamName(userEntity.getTeamName());
                    userMarkEntity.setType(type);
                    markEntity.getUserMarkEntities().add(userMarkEntity);
                    markRepository.save(markEntity);
                }
            }
        }
    }
}
