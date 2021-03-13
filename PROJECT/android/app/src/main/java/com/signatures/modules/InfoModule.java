package com.signatures.modules;

import com.signatures.services.CompanyService;
import com.signatures.services.DocumentService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InfoModule {

    @Provides
    @Singleton
    public DocumentService provideDocumentService() {
        return new DocumentService();
    }

    @Provides
    @Singleton
    public CompanyService provideCompanyService() {
        return new CompanyService();
    }
}
