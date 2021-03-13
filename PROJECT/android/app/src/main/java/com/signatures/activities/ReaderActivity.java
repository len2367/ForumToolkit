package com.signatures.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.signatures.App;
import com.signatures.R;
import com.signatures.models.responses.DocumentServerResponse;
import com.signatures.services.DocumentService;
import com.signatures.services.RestService;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class ReaderActivity extends AppCompatActivity {
    private final static String TAG = "ReaderActivity";

    @Inject
    public DocumentService documentService;

    @Inject
    public RestService restService;

    private Button signBtn;
    private PDFView pdfView;
    private ProgressBar progressBar;

    private DocumentServerResponse documentServerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hidding title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_reader);

        App.getComponent().injectsReaderActivity(this);

        initUI();

        setDocumentServerResponse();

        showPdfDoc();
    }

    private void startProgressBar(Long downloadId) {
        progressBar.setVisibility(View.VISIBLE);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(downloadId);
                Cursor cursor = ((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).query(q);
                cursor.moveToFirst();
                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                cursor.close();
                final int dl_progress = (bytes_downloaded * 100 / bytes_total);
                runOnUiThread(() -> progressBar.setProgress(dl_progress));

            }

        }, 0, 10);
    }

    private void showPdfDoc() {
        if (!isDocumentDownloaded(documentServerResponse.getId())) {
            startProgressBar(
                    restService.downloadDocument(
                            documentServerResponse.getId()
                    )
            );

            registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            progressBar.setVisibility(View.INVISIBLE);
                            showDocument(documentServerResponse.getId());
                        }
                    },
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            );
        } else {
            showDocument(documentServerResponse.getId());
        }
    }

    private void initUI() {
        signBtn = findViewById(R.id.signbtn);
        signBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignActivity.class);
            intent.putExtra("documentId", documentServerResponse.getId());
            startActivity(intent);
            finish();
        });

        pdfView = findViewById(R.id.pdfv);
        progressBar = findViewById(R.id.progress_download);
    }

    private void setDocumentServerResponse() {
        Intent intent = getIntent();
        documentServerResponse = documentService.getByPosition(
                intent.getIntExtra("position", -1)
        );
    }

    private boolean isDocumentDownloaded(Long documentId) {
        return new File(
                Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS +
                        "/SignatureService/" + documentId + ".pdf"
        ).exists();
    }

    private void showDocument(Long documentId) {
        pdfView.setVisibility(View.VISIBLE);
        pdfView.fromFile(
                new File(
                        Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS +
                                "/SignatureService/" + documentId + ".pdf"
                )
        ).defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .scrollHandle(null)
                .onPageChange((page, pageCount) -> {
                    if (page + 1 == pageCount) signBtn.setVisibility(View.VISIBLE);
                })
                .load();
    }
}