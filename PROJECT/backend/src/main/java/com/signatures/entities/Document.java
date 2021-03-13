package com.signatures.entities;

import com.signatures.responses.DocumentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "documents")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document extends AbstractAuditable implements Responsible<DocumentResponse> {

    private String name;

    private String description;

    private String language;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "diskName", columnDefinition = "VARCHAR(255)", updatable = false, nullable = false)
    private String diskName;

    @Override
    public DocumentResponse generateResponse() {
        return new DocumentResponse(
                super.getId(),
                name,
                description,
                language
        );
    }

    public Document(Long id) {
        super(id);
    }

    @PrePersist
    private void initializeUUID() {
        diskName = UUID.randomUUID().toString() + ".png";
    }
}
