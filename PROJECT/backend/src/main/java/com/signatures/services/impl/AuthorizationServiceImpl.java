package com.signatures.services.impl;

import com.signatures.entities.Company;
import com.signatures.exceptions.BadCodeException;
import com.signatures.repositories.CompanyRepository;
import com.signatures.services.interfaces.AuthorizationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationServiceImpl implements AuthorizationService<Long, Company> {
    private final CompanyRepository companyRepository;

    @Override
    public Company getByIdAndCode(Long id, String code) throws BadCodeException {
        Company company = companyRepository.findById(id).orElseThrow(BadCodeException::new);

        if (!isCodeEquals(company.getCode(), code)) throw new BadCodeException();

        return company;
    }

    @Override
    public boolean isCodeEquals(String code1, String code2) {
        return code1.equals(code2);
    }
}
