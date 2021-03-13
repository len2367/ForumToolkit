package com.signatures.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.signatures.models.responses.CompanyServerResponse;

import java.lang.reflect.Type;
import java.util.List;

// TODO: доделать
public class CompanyService {
    private final static String TAG = "CompanyService";

    private CompanyServerResponse company;
    private List<String> languages;

    private final Gson gson;

    public CompanyService() {
        this.gson = new GsonBuilder().create();
    }

    public CompanyServerResponse getCompany() {
        return company;
    }

    private void setLanguages(String languagesStr) {
        Type founderListType = new TypeToken<List<String>>() {
        }.getType();
        this.languages = (List<String>) gson.fromJson(languagesStr, founderListType);
    }

    public void setCompany(CompanyServerResponse company) {

        this.company = company;
        setLanguages(company.getLanguages());
    }

    public List<String> getLanguages() {
        return languages;
    }
}
