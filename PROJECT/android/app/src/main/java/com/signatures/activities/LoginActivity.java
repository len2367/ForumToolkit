package com.signatures.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.signatures.App;
import com.signatures.R;
import com.signatures.UICallback;
import com.signatures.models.responses.AuthorizationServerResponse;
import com.signatures.models.responses.ServerResponse;
import com.signatures.models.responses.errors.ServerErrorResponse;
import com.signatures.services.CompanyService;
import com.signatures.services.RestService;

import java.io.IOException;

import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity {

    @Inject
    public RestService restService;

    @Inject
    public CompanyService companyService;

    private Button signButton;
    private EditText companyEditText;
    private EditText keyEditText;
    private TextView statusMessage;

    private boolean isCompanyIdClicked;
    private boolean isKeyClicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hidding title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();

        App.getComponent().injectsLoginActivity(this);
    }

    private void initUI() {
        setContentView(R.layout.activity_login);

        signButton = findViewById(R.id.sign_in);
        signButton.setOnClickListener(v -> signInOnClick());

        companyEditText = findViewById(R.id.company_id);
        companyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCompanyIdClicked = true;
                signButton.setEnabled(isKeyClicked);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        keyEditText = findViewById(R.id.secret_key);
        keyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isKeyClicked = true;
                signButton.setEnabled(isCompanyIdClicked);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        statusMessage = findViewById(R.id.status_message);
    }

    private Pair<Long, String> getLoginInfo() {
        String companyId = companyEditText.getText().toString();
        if (companyId.trim().isEmpty()) {
            showErrorMessage("Company id can't be empty");
            return null;
        }

        String key = keyEditText.getText().toString();
        if (key.trim().isEmpty()) {
            showErrorMessage("Secret key can't be empty");
            return null;
        }

        return Pair.create(
                Long.valueOf(companyId),
                key
        );
    }

    private void signInOnClick() {
        Pair<Long, String> loginInfo = getLoginInfo();
        if (loginInfo == null) return;

        try {
            restService.login(
                    loginInfo.first,
                    loginInfo.second,

                    new UICallback() {

                        @Override
                        public void onFailure(ServerErrorResponse serverErrorResponse) {
                            runOnUiThread(() -> {
                                if (serverErrorResponse == null) {
                                    showErrorMessage("Connection Error");
                                } else {
                                    showErrorMessage(serverErrorResponse.getMessage());
                                }

                                clearFields();
                            });
                        }

                        @Override
                        public void onSuccess(ServerResponse serverResponse) {
                            runOnUiThread(() -> {
                                showSuccessMessage(R.string.success);
                            });

                            companyService.setCompany(((AuthorizationServerResponse) serverResponse).getCompany());


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }

            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearFields();
        signButton.setEnabled(false);
    }

    private void clearFields() {
        companyEditText.setText("");
        keyEditText.setText("");
    }

    private void showErrorMessage(String msg) {
        statusMessage.setText(msg);
        statusMessage.setTextColor(Color.RED);
    }

    private void showErrorMessage(@StringRes int resid) {
        statusMessage.setText(resid);
        statusMessage.setTextColor(Color.RED);
    }

    private void showSuccessMessage(String msg) {
        statusMessage.setText(msg);
        statusMessage.setTextColor(Color.GREEN);
    }

    private void showSuccessMessage(@StringRes int resid) {
        statusMessage.setText(resid);
        statusMessage.setTextColor(Color.GREEN);
    }
}