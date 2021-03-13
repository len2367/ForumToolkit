package com.signatures.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.signatures.App;
import com.signatures.R;
import com.signatures.UICallback;
import com.signatures.models.responses.ServerResponse;
import com.signatures.models.responses.errors.ServerErrorResponse;
import com.signatures.services.RestService;
import com.signatures.ui.DrawingView;

import java.io.File;

import javax.inject.Inject;

public class SignActivity extends AppCompatActivity {

    private Button sendBtn;
    private DrawingView drawingView;
    private EditText fioEditTxt;
    private TextView errorView;
    private Button clearBtn;

    private Long documentId;


    private boolean isFioClicked = false;
    private boolean isSignatureClicked = false;

    @Inject
    public RestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hidding title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign);

        App.getComponent().injectsSignActivity(this);

        Intent intent = getIntent();
        documentId = intent.getLongExtra("documentId", -1);

        initUI();
    }

    @Nullable
    private String getFio() {
        String fio = fioEditTxt.getText().toString();
        if (fio.trim().isEmpty()) {
            showErrorMessage("Fio can't be empty!");
            isFioClicked = false;
            sendBtn.setEnabled(false);
            return null;
        }
        return fio;
    }

    private void showErrorMessage(String msg) {
        errorView.setText(msg);
        errorView.setTextColor(Color.RED);
    }

    // Проверка на наличие подписи как при логине
    private void initUI() {
        drawingView = findViewById(R.id.drawview);
        sendBtn = findViewById(R.id.sendbtn);
        fioEditTxt = findViewById(R.id.fio);
        errorView = findViewById(R.id.error_sign);
        clearBtn = findViewById(R.id.clear_text);

        showKeyboard();

        clearBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignActivity.class);
            intent.putExtra("documentId", documentId);
            startActivity(intent);
            finish();
        });

        drawingView.setOnTouchListener(() -> {
            isSignatureClicked = true;
            sendBtn.setEnabled(isFioClicked);
        });

        fioEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isFioClicked = true;
                sendBtn.setEnabled(isSignatureClicked);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn.setOnClickListener(v -> {
            File file = drawingView.getImage(
                    v.getWidth(),
                    v.getHeight()
            );


            restService.uploadSignature(
                    documentId,
                    getFio(),
                    file,
                    new UICallback() {
                        @Override
                        public void onFailure(@Nullable ServerErrorResponse serverErrorResponse) {
                            runOnUiThread(() ->
                            {
                                Toast.makeText(getApplicationContext(), "CONNECTION ERROR", Toast.LENGTH_LONG).show();
                                finish();
                            });
                        }

                        @Override
                        public void onSuccess(@Nullable ServerResponse serverResponse) {
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_LONG).show();
                                finish();
                            });
                        }
                    }
            );
        });
    }

    private void showKeyboard() {
        fioEditTxt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}