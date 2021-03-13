package com.signatures.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.signatures.App;
import com.signatures.R;
import com.signatures.UICallback;
import com.signatures.models.responses.CompanyServerResponse;
import com.signatures.models.responses.DocumentListServerResponse;
import com.signatures.models.responses.ServerResponse;
import com.signatures.models.responses.errors.ServerErrorResponse;
import com.signatures.services.CompanyService;
import com.signatures.services.DocumentService;
import com.signatures.services.RestService;
import com.signatures.ui.LanguageButton;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    @Inject
    public RestService restService;

    @Inject
    public DocumentService documentService;

    @Inject
    public CompanyService companyService;

    private ListView docsView;
    private Button logoutBtn;
    private LanguageButton languageBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //hidding title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!requestPermissions()) {
            Toast.makeText(this, "Аpp will not work correctly without permissions", Toast.LENGTH_LONG).show();
        }

        App.getComponent().injectsMainActivity(this);

        initUI();

        restService.setRedirectToLoginPage(
                this::redirectToLoginPage
        );

        new Thread(() -> {
            restService.init();

            CompanyServerResponse companyServerResponse = restService.getCompany();
            if (companyServerResponse == null) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                });
                return;
            }
            companyService.setCompany(companyServerResponse);

            initLanguageBtn();

            restService.getDocuments(
                    new UICallback() {
                        @Override
                        public void onFailure(ServerErrorResponse serverErrorResponse) {
                            if (serverErrorResponse == null) {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
                                });
                            }
                        }

                        @Override
                        public void onSuccess(ServerResponse serverResponse) {
                            runOnUiThread(() -> {
                                DocumentListServerResponse docs = (DocumentListServerResponse) serverResponse;
                                documentService.setDocuments(docs);

                                docsView.setAdapter(
                                        new SimpleAdapter(
                                                getApplicationContext(),
                                                documentService.getDocumentsInfoByLang(
                                                        languageBtn.getCurrentLanguage()
                                                ),
                                                android.R.layout.simple_list_item_2,
                                                new String[]{"Name", "Description"},
                                                new int[]{android.R.id.text1, android.R.id.text2}
                                        )
                                );

                                docsView.setOnItemClickListener(
                                        (parent, view, position, id) -> startReaderActivity(position)
                                );
                            });
                        }
                    }
            );
        }).start();
    }

    private boolean requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1
        );

        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                &&
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void initUI() {
        docsView = findViewById(R.id.docs);

        logoutBtn = findViewById(R.id.logoutbtn);
        logoutBtn.setOnClickListener(v -> {
            Log.i(TAG, "logout pressed");

            restService.logout();

            redirectToLoginPage();
        });

        languageBtn = findViewById(R.id.langbtn);
    }

    private void initLanguageBtn() {
        runOnUiThread(() ->
        {
            languageBtn.setLanguages(companyService.getLanguages());
            languageBtn.setOnClickListener(v -> {
                docsView.setAdapter(
                        new SimpleAdapter(
                                getApplicationContext(),
                                documentService.getDocumentsInfoByLang(
                                        languageBtn.getCurrentLanguage()
                                ),
                                android.R.layout.simple_list_item_2,
                                new String[]{"Name", "Description"},
                                new int[]{android.R.id.text1, android.R.id.text2}
                        )
                );
            });
        });
    }

    private void redirectToLoginPage() {
        Log.i(TAG, "redirect to login activity");

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startReaderActivity(int position) {
        Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}