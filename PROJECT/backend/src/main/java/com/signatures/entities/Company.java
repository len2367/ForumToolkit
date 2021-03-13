package com.signatures.entities;

import com.signatures.responses.CompanyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "companies")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company extends AbstractAuditable implements Responsible<CompanyResponse> {
    private String name;
    private String code;
    private String languages;

    public Company(Long id) {
        super(id);
    }

    @Override
    public CompanyResponse generateResponse() {
        return new CompanyResponse(
                name,
                languages
        );
    }
}
