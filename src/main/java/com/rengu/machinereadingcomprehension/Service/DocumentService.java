package com.rengu.machinereadingcomprehension.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rengu.machinereadingcomprehension.Entity.DocumentEntity;
import com.rengu.machinereadingcomprehension.Repository.DocumentRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-10-12 13:04
 **/

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MarkService markService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, MarkService markService) {
        this.documentRepository = documentRepository;
        this.markService = markService;
    }

    public void saveDocuments(MultipartFile doc) throws IOException {
        File docFile = new File(FileUtils.getTempDirectoryPath() + File.separator + doc.getOriginalFilename());
        FileUtils.copyInputStreamToFile(doc.getInputStream(), docFile);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(docFile);
        Iterator<JsonNode> rootNodeIterator = rootNode.elements();
        while (rootNodeIterator.hasNext()) {
            JsonNode sonNode = rootNodeIterator.next();
            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setId(sonNode.get("article_id").asInt());
            documentEntity.setType(sonNode.get("article_type").asText());
            documentEntity.setTitle(sonNode.get("article_title").asText());
            documentEntity.setContent(sonNode.get("article_content").asText());
            documentEntity.setMarkEntities(markService.saveMarks(sonNode.get("questions")));
            documentRepository.save(documentEntity);
        }
    }

    public Page<DocumentEntity> getDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

    public DocumentEntity getDocumentById(int documentId) {
        return documentRepository.findById(documentId).get();
    }
}
