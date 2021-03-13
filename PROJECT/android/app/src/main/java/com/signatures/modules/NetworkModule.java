package com.signatures.modules;

import com.signatures.services.RestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    public RestService provideRestService() {
        return new RestService();
    }
}
