package com.signatures.services.interfaces.data;

import com.signatures.entities.AbstractAuditable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SignatureService<ID, T extends AbstractAuditable> extends DataService<ID, T> {

    void saveToDisk(String fileName, MultipartFile multipartFile) throws IOException;
}
