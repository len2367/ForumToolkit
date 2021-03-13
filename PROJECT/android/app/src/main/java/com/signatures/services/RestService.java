package com.signatures.services;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.signatures.UICallback;
import com.signatures.models.responses.AuthorizationServerResponse;
import com.signatures.models.responses.CompanyServerResponse;
import com.signatures.models.responses.DocumentListServerResponse;
import com.signatures.models.responses.DocumentServerResponse;
import com.signatures.models.responses.RefreshServerResponse;
import com.signatures.models.responses.ServerResponse;
import com.signatures.models.responses.errors.ServerErrorResponse;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.UnknownHostException;


public class RestService {
    private final static String TAG = "RestService";

    private final String baseUrl = "https://signatures.pw";

    private final OkHttpClient client;
    private final Gson gson;
    private final MediaType signatureMediaType;

    private Runnable redirectToLoginPage;
    private SharedPreferences.Editor editor;
    private DownloadManager downloadManager;

    private String token;

    public RestService() {
        this.client = new OkHttpClient();

        this.gson = new GsonBuilder().create();

        this.signatureMediaType = MediaType.parse("image/png");
    }

    public void init() {
        if (token != null) {
            refresh();
        } else {
            redirectToLoginPage.run();
        }
    }

    public void setRedirectToLoginPage(Runnable redirectToLoginPage) {
        this.redirectToLoginPage = redirectToLoginPage;
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @SuppressLint("CommitPrefEdits")
    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", null);
    }

    private void saveToken(String token) {
        this.token = token;
        editor.putString("token", token);
        editor.apply();
    }

    public void login(Long companyId, String code, UICallback uiCallback) throws IOException {
        Log.i(TAG, "login start");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/auth").newBuilder();
        urlBuilder.addQueryParameter("id", companyId.toString());
        urlBuilder.addQueryParameter("code", code);

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                uiCallback.onFailure(null);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                ServerResponse serverResponse;
                if (response.isSuccessful()) {
                    Log.i(TAG, "login successful");

                    serverResponse = gson.fromJson(response.body().string(), AuthorizationServerResponse.class);

                    token = ((AuthorizationServerResponse) serverResponse).getToken();
                    saveToken(token);

                    uiCallback.onSuccess(serverResponse);
                } else {
                    serverResponse = gson.fromJson(response.body().string(), ServerErrorResponse.class);

                    Log.i(TAG, "login failed: " + ((ServerErrorResponse) serverResponse).getMessage());

                    uiCallback.onFailure((ServerErrorResponse) serverResponse);
                }
            }
        });
    }

    public void logout() {
        Log.i(TAG, "logout has been requested");
        editor.remove("token");
        editor.commit();
    }

    private void refresh() {
        Log.i(TAG, "refresh has been requested");

        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(baseUrl + "/auth/refresh")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            Log.i(TAG, "Response: " + body);

            if (response.isSuccessful()) {
                RefreshServerResponse refreshServerResponse = gson.fromJson(
                        body,
                        RefreshServerResponse.class
                );

                saveToken(refreshServerResponse.getToken());

                Log.i(TAG, "token has been refreshed");
            } else {
                if (response.code() == 401 || response.code() == 408) {
                    Log.i(TAG, "Invalid token - " + response.code());
                    logout();
                    redirectToLoginPage.run();
                } else if (response.code() == 425) {
                    Log.i(TAG, "token is okay");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDocuments(UICallback uiCallback) {
        Log.i(TAG, "Documents list has been requested");

        if (token == null) {
            uiCallback.onFailure(null);
            Log.i(TAG, "Token is null");
            return;
        }

        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(baseUrl + "/documents")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                String body = response.body().string();

                if (response.isSuccessful()) {
                    Log.i(TAG, "Get documents is successful");
                    Log.i(TAG, "Response: " + body);

                    try {
                        Type founderListType = new TypeToken<DocumentListServerResponse>() {
                        }.getType();
                        DocumentListServerResponse docs = gson.fromJson(body, founderListType);

                        for (DocumentServerResponse documentServerResponse : docs) {
                            Log.i(TAG, "received document: " + documentServerResponse.toString());
                        }

                        uiCallback.onSuccess(docs);

                    } catch (Exception e) {
                        if (e instanceof UnknownHostException) {
                            uiCallback.onFailure(null);
                        }
                        e.printStackTrace();
                    }
                } else {
                    ServerErrorResponse serverErrorResponse = gson.fromJson(body, ServerErrorResponse.class);

                    Log.i(TAG, "Get documents failed. Error: " + serverErrorResponse.getMessage());

                    if (serverErrorResponse.getStatus() == 401) {
                        redirectToLoginPage.run();
                    } else if (serverErrorResponse.getStatus() == 403) {
                        refresh();
                        getDocuments(uiCallback);
                    }
                }
            }
        });
    }

    public long downloadDocument(Long documentId) {
        Log.i(TAG, "Start downloading. File's id " + documentId);

        if (token == null) {
            Log.i(TAG, "Token is null");
            return -1;
        }


        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(baseUrl + "/documents/download/" + documentId)
        );
        request.addRequestHeader("Authorization", token);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SignatureService/" + documentId + ".pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
        request.allowScanningByMediaScanner();// if you want to be available from media players
        return downloadManager.enqueue(request);
    }

    public void uploadSignature(Long documentId, String fio, File file, UICallback uiCallback) {
        if (fio == null) {
            return;
        }

        Log.i(TAG, "Start uploading signature");

        if (token == null) {
            Log.i(TAG, "Token is null");
            uiCallback.onFailure(null);
            return;
        }


        RequestBody fileBody = RequestBody.create(signatureMediaType, file);
        //Request body
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"file\"; filename=\"file\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(baseUrl + "/signatures/" + documentId + "?fio=" + fio)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                file.delete();
                uiCallback.onFailure(null);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    file.delete();
                    Log.i(TAG, "upload successful");
                    uiCallback.onSuccess(null);
                } else if (response.code() == 401 || response.code() == 404) {
                    file.delete();
                    redirectToLoginPage.run();
                } else if (response.code() == 403) {
                    refresh();
                    uploadSignature(documentId, fio, file, uiCallback);
                } else {
                    file.delete();
                    Log.i(TAG, "Error: " + response.body().string());
                    uiCallback.onFailure(null);
                }
            }
        });
    }

    @Nullable
    public CompanyServerResponse getCompany() {
        Log.i(TAG, "Start getting company info");

        if (token == null) {
            Log.i(TAG, "Token is null");
            return null;
        }

        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(baseUrl + "/company")
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                Log.i(TAG, "Succes getting company");

                return gson.fromJson(
                        response.body().string(),
                        CompanyServerResponse.class
                );
            } else {
                Log.i(TAG, "Error getting company: " + response.body().string());

                if (response.code() == 401 || response.code() == 404) {
                    redirectToLoginPage.run();
                    return null;
                } else if (response.code() == 403) {
                    refresh();
                    return getCompany();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }
}
