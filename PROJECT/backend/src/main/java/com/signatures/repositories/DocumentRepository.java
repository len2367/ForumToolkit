package com.signatures.repositories;

import com.signatures.entities.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("select d from Document d where d.company.id=:cid and d.id=:id")
    Optional<Document> findByIdAndCompanyId(Long id, Long cid);

    @Query("select d from Document d where d.company.id=:cid")
    List<Document> getDocumentsByCompanyId(Long cid, Pageable pageable);

    boolean existsByCompanyIdAndId(Long companyId, Long id);
}
