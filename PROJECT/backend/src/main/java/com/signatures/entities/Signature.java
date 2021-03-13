package com.signatures.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "signatures")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Signature extends AbstractAuditable {

    @Column(name = "fio", updatable = false, nullable = false)
    private String fio;

    @Column(name = "diskName", columnDefinition = "VARCHAR(255)", updatable = false, nullable = false)
    private String diskName;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    public Signature(String fio, Document document) {
        this.fio = fio;
        this.document = document;
    }

    @PrePersist
    private void initializeUUID() {
        diskName = UUID.randomUUID().toString() + ".png";
    }
}
