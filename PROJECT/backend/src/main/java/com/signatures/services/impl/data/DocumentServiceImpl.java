package com.signatures.services.impl.data;

import com.signatures.entities.Document;
import com.signatures.exceptions.DocumentNotFoundException;
import com.signatures.properties.DocumentProperties;
import com.signatures.repositories.DocumentRepository;
import com.signatures.services.interfaces.data.DocumentService;
import com.sun.istack.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentServiceImpl implements DocumentService<Long, Document, Long> {
    private final DocumentRepository documentRepository;
    private final DocumentProperties documentProperties;

    @PostConstruct
    private void createFilesFolderIfNotExists() {
        new File(documentProperties.getPath()).mkdir();
    }

    @Override
    public Document getById(Long id) {
        return documentRepository.getOne(id);
    }

    @Override
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public Document save(Document document) {
        log.info("Saving new document");
        documentRepository.save(document);
        log.info(
                String.format(
                        "New document: id - %d diskname - %s",
                        document.getId(),
                        document.getDiskName()
                )
        );

        return document;
    }

    @Override
    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }

    @Override
    public byte[] getDocumentBytesById(Long id, Long cid) throws IOException, DocumentNotFoundException {
        Document document = documentRepository.findByIdAndCompanyId(id, cid).orElseThrow(DocumentNotFoundException::new);
        return new FileInputStream(
                documentProperties.getPath() + document.getDiskName()
        ).readAllBytes();
    }

    @Override
    public List<Document> getDocumentsByCompanyId(Long cid, @Nullable Pageable pageable) {
        return documentRepository.getDocumentsByCompanyId(cid, pageable);
    }

    @Override
    public boolean hasCompanyAccessToDocument(Long companyId, Long documentId) {
        return documentRepository.existsByCompanyIdAndId(companyId, documentId);
    }
}
