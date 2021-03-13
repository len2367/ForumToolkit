package com.signatures;

import com.signatures.activities.LoginActivity;
import com.signatures.activities.MainActivity;
import com.signatures.activities.ReaderActivity;
import com.signatures.activities.SignActivity;
import com.signatures.modules.InfoModule;
import com.signatures.modules.NetworkModule;
import com.signatures.services.CompanyService;
import com.signatures.services.RestService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetworkModule.class, InfoModule.class})
public interface AppComponent {

    RestService getRestService();

    CompanyService getCompanyService();

    void injectsLoginActivity(LoginActivity loginActivity);

    void injectsMainActivity(MainActivity mainActivity);

    void injectsReaderActivity(ReaderActivity readerActivity);

    void injectsSignActivity(SignActivity signActivity);
}
