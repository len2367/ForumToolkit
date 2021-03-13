package com.signatures.services.interfaces.data;

import com.signatures.entities.AbstractAuditable;
import com.signatures.exceptions.DocumentNotFoundException;
import com.sun.istack.Nullable;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface DocumentService<ID, T extends AbstractAuditable, CID> extends DataService<ID, T> {

    byte[] getDocumentBytesById(ID id, CID cid) throws IOException, DocumentNotFoundException;

    List<T> getDocumentsByCompanyId(CID cid, @Nullable Pageable pageable);

    boolean hasCompanyAccessToDocument(CID cid, ID id);
}
