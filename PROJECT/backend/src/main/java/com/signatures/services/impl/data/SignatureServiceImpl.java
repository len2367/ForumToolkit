package com.signatures.services.impl.data;

import com.signatures.entities.Signature;
import com.signatures.properties.SignaturesProperties;
import com.signatures.repositories.SignatureRepository;
import com.signatures.services.interfaces.data.SignatureService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignatureServiceImpl implements SignatureService<Long, Signature> {
    private final SignatureRepository signatureRepository;
    private final SignaturesProperties signaturesProperties;

    @PostConstruct
    private void createFilesFolderIfNotExists() {
        new File(signaturesProperties.getPath()).mkdir();
    }

    @Override
    public Signature getById(Long id) {
        return signatureRepository.getOne(id);
    }

    @Override
    public Optional<Signature> findById(Long id) {
        return signatureRepository.findById(id);
    }

    @Override
    public Signature save(Signature signature) {
        log.info("Saving new signature");
        signatureRepository.save(signature);
        log.info(
                String.format(
                        "New signature: id - %d diskname - %s",
                        signature.getId(),
                        signature.getDiskName()
                )
        );

        return signature;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting signature " + id);

        Signature signature = getById(id);
        if (signature != null) {
            try {
                Files.deleteIfExists(Paths.get(""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveToDisk(String fileName, MultipartFile multipartFile) throws IOException {
        log.info("Saving to disk " + fileName);
        multipartFile.transferTo(
                new File(signaturesProperties.getPath() + fileName)
        );
        log.info("Saved " + fileName);
    }
}
