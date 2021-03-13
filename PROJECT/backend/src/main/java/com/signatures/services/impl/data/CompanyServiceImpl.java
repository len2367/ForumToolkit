package com.signatures.services.impl.data;

import com.signatures.entities.Company;
import com.signatures.repositories.CompanyRepository;
import com.signatures.services.interfaces.data.CompanyService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    @Override
    public Company getById(Long companyId) {
        return companyRepository.getOne(companyId);
    }

    @Override
    public Optional<Company> findById(Long companyId) {
        return companyRepository.findById(companyId);
    }

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public void deleteById(Long companyId) {
        companyRepository.deleteById(companyId);
    }
}
